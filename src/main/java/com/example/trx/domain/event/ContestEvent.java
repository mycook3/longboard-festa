package com.example.trx.domain.event;

import com.example.trx.domain.event.round.Round;
import com.example.trx.domain.event.round.RoundStatus;
import com.example.trx.domain.event.round.ScoreBasedRound;
import com.example.trx.domain.event.round.TournamentRound;
import com.example.trx.domain.judge.Judge;
import com.example.trx.domain.event.round.run.Run;
import com.example.trx.domain.user.Participant;
import com.example.trx.domain.user.Participation;
import com.example.trx.domain.user.ParticipationStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

//종목 정보
@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ContestEvent {//Aggregate Root
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // 수준
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private Division division;

  // 종목명
  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private DisciplineCode disciplineCode;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  @Builder.Default
  private ContestEventStatus contestEventStatus = ContestEventStatus.NOT_INITIALIZED;

  @Enumerated(EnumType.STRING)
  @Builder.Default
  private RoundProgressionType progressionType = RoundProgressionType.SCORE_BASED;

  // 현재 진행 중인 라운드
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "current_round_id")
  private Round currentRound;

  @OneToMany(mappedBy = "contestEvent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @Builder.Default
  private List<Round> rounds = new ArrayList<>();

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @Builder.Default
  private List<Participation> participations = new ArrayList<>();

  public Round addRound(String roundName, Integer limit, Integer runPerParticipant) {
      Round round;

    if (this.progressionType == RoundProgressionType.TOURNAMENT) {
      round = TournamentRound.builder()
          .contestEvent(this)
          .name(roundName)
          .status(RoundStatus.BEFORE)
          .runsPerParticipant(runPerParticipant)
          .participantLimit(limit)
          .build();
    } else {
      round = ScoreBasedRound.builder()
          .contestEvent(this)
          .name(roundName)
          .status(RoundStatus.BEFORE)
          .runsPerParticipant(runPerParticipant)
          .participantLimit(limit)
          .build();
    }

    rounds.add(round);
    return round;
  }

  /**
   * 종목을 시작 상태로 변경하고, 현재 진행 라운드를 첫 번째 라운드로 설정합니다.
   * 첫 번째 라운드에 초기 참가자들을 모두 추가합니다.
   */
  public void init() {
    if (contestEventStatus != ContestEventStatus.NOT_INITIALIZED) throw new IllegalStateException("이미 초기화가 완료되었습니다");
    if (rounds.isEmpty()) throw new IllegalStateException("No round has been added");
    contestEventStatus = ContestEventStatus.READY;
    currentRound = rounds.get(0);

    List<Participant> activeParticipants = participations.stream()
        .filter(p -> p.getStatus() == ParticipationStatus.ACTIVE)
        .map(Participation::getParticipant)
        .toList();

    currentRound.addParticipants(activeParticipants);
  }

  /**
   * 현재 진행 중인 라운드가 등록된 마지막 라운드고 해당 라운드의 마지막 시도까지 마친 경우 종목을 종료합니다
   */
  public void end() {
    if (currentRound == rounds.get(rounds.size() - 1) && currentRound.canBeCompleted()) {
      currentRound.markAsCompleted();
      this.contestEventStatus = ContestEventStatus.COMPLETED;
    }
  }

  public Run getCurrentRun() {
    if (currentRound != null && currentRound.getCurrentRun() != null) return currentRound.getCurrentRun();
    if (currentRound == null) throw new IllegalStateException("no currentRound set");
    throw new IllegalStateException("no currentRun set");
  }

  /**
   * 현재 시도를 다음으로 넘깁니다
   * 현재 active 상태의 심사위원 전원이 점수를 제출한 경우에만 넘길 수 있습니다
   * 라운드의 마지막 순서인 경우에는 예외를 던집니다
   */
  public void proceedRunOrMatch() {
    if (contestEventStatus != ContestEventStatus.IN_PROGRESS) throw new IllegalStateException("시작하지 않았거나 종료된 종목입니다.");
    if (rounds.isEmpty()) throw new IllegalStateException("No round has been set");

    currentRound.proceed();
  }

   /**
   * RoundStatus.BEFORE 상태인 가장 첫 라운드를 찾습니다.
   */
  public Optional<Round> findNextRound() {
    return rounds.stream()
          .filter(round -> round.getStatus().equals(RoundStatus.BEFORE))
          .findFirst();
  }

  /**
   * 다음 라운드로 진행할 수 있다면 진행합니다
   * 만약 종목의 마지막 라운드라면 현재 라운드를 완료 상태로 전환하기만 합니다.
   */
  public void proceedRound() {
    if (contestEventStatus == ContestEventStatus.NOT_INITIALIZED) throw new IllegalStateException("아직 초기화되지 않은 종목입니다");

    if (contestEventStatus == ContestEventStatus.COMPLETED) throw new IllegalStateException("종료된 종목입니다");

    if (rounds.isEmpty()) throw new IllegalStateException("No round has been started");

    Round nextRound = findNextRound().orElse(null);
    boolean isLast = nextRound == null;

    if (!currentRound.canBeCompleted()) return;

    currentRound.markAsCompleted();

    if (isLast) return;

    List<Participant> survivors = currentRound.getSurvivors(nextRound);
    nextRound.addParticipants(survivors);
    nextRound.markAsInProgress();

    currentRound = nextRound;
  }

  /**
   * 현재 라운드를 시작합니다.
   * 현재 활성화된 심사위원 목록을 가져와 그 수 * 참가자 별 시도 횟수만큼의 Run 객체를 생성, 저장합니다
   */
  public void startCurrentRound(List<Judge> activeJudges) {
    if (contestEventStatus == ContestEventStatus.NOT_INITIALIZED) throw new IllegalStateException("아직 초기화되지 않은 종목입니다");

    if (contestEventStatus == ContestEventStatus.COMPLETED) throw new IllegalStateException("종료된 종목입니다");
    if (currentRound == null) throw new IllegalStateException("no currentRound set");

    if (currentRound.getStatus() != RoundStatus.BEFORE) throw new IllegalStateException("이미 진행 중이거나 종료된 라운드입니다");

    contestEventStatus = ContestEventStatus.IN_PROGRESS;
    currentRound.start(activeJudges);
  }
}
