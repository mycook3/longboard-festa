package com.example.trx.domain.event;

import com.example.trx.domain.event.round.Round;
import com.example.trx.domain.event.round.RoundStatus;
import com.example.trx.domain.judge.Judge;
import com.example.trx.domain.event.round.run.Run;
import com.example.trx.domain.user.Participant;
import com.example.trx.domain.user.Participation;
import com.example.trx.domain.user.ParticipationStatus;
import jakarta.persistence.CascadeType;
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
  private Division division;

  // 종목명
  @Enumerated(EnumType.STRING)
  private DisciplineCode disciplineCode;

  @Enumerated(EnumType.STRING)
  @Builder.Default
  private ContestEventStatus contestEventStatus = ContestEventStatus.READY;

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
    Round round = Round.builder()
        .contestEvent(this)
        .name(roundName)
        .status(RoundStatus.BEFORE)
        .participantLimit(limit)
        .runsPerParticipant(runPerParticipant)
        .build();

    rounds.add(round);
    return round;
  }

  public void init() {
    if (contestEventStatus != ContestEventStatus.READY) throw new IllegalStateException("이미 진행 중이거나 종료된 종목입니다");
    if (rounds.isEmpty()) throw new IllegalStateException("No round has been added");
    contestEventStatus = ContestEventStatus.IN_PROGRESS;

    currentRound = rounds.get(0);
    currentRound.markAsInProgress();

    List<Participant> activeParticipants = participations.stream()
        .filter(p -> p.getStatus() == ParticipationStatus.ACTIVE)
        .map(Participation::getParticipant)
        .toList();

    currentRound.addParticipants(activeParticipants);
  }

  public void startFirstRound(List<Judge> judges) {
    currentRound.start(judges);
  }

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

  public void proceedRun(int activeJudgesCount) {
    if (contestEventStatus != ContestEventStatus.IN_PROGRESS) throw new IllegalStateException("시작하지 않았거나 종료된 종목입니다.");
    if (rounds.isEmpty()) throw new IllegalStateException("No round has been set");

    Run currentRun = this.getCurrentRun();

    if (currentRun.canBeCompleted(activeJudgesCount)) {
      currentRun.markAsDone();
      Run nextRun = currentRound.findNextRun()
          .orElseThrow(() -> new IllegalStateException("해당 라운드의 마지막 시도입니다."));

      currentRound.moveToRun(nextRun);
    }
    else throw new IllegalStateException("일부 심사위원이 점수를 제출하지 않았습니다.");//TODO
  }

  public void proceedRound() {
    if (contestEventStatus != ContestEventStatus.IN_PROGRESS) throw new IllegalStateException("시작하지 않았거나 종료된 종목입니다.");
    if (rounds.isEmpty()) throw new IllegalStateException("No round has been started");

    Round nextRound = findNextRound().orElse(null);
    boolean isLast = nextRound == null;

    if (isLast) currentRound.markAsCompleted();
    else if (currentRound.canBeCompleted()) {
      currentRound.markAsCompleted();

      List<Participant> survivors = currentRound.getSurvivors(nextRound);
      nextRound.addParticipants(survivors);
      nextRound.markAsInProgress();

      currentRound = nextRound;
    }
  }

  public Optional<Round> findNextRound() {
    return rounds.stream()
          .filter(round -> round.getStatus().equals(RoundStatus.BEFORE))
          .findFirst();
  }

}
