package com.example.trx.domain.event;

import com.example.trx.domain.judge.JudgeStatus;
import com.example.trx.domain.judge.Judging;
import com.example.trx.domain.run.Run;
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
import jakarta.persistence.OrderBy;
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

  // 종목 정보
  @Enumerated(EnumType.STRING)
  private DisciplineCode disciplineCode;

  @Enumerated(EnumType.STRING)
  @Builder.Default
  private ContestEventStatus contestEventStatus = ContestEventStatus.READY;

  // 현재 진행 중인 라운드
  @OneToOne(fetch = FetchType.LAZY)
  private Round currentRound;

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @Builder.Default
  private List<Round> rounds = new ArrayList<>();

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @Builder.Default
  private List<Judging> judgings= new ArrayList<>();

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @Builder.Default
  private List<Participation> participations = new ArrayList<>();

  public void addRound(String roundName, Integer limit) {
    Round round = Round.builder()
        .contestEvent(this)
        .name(roundName)
        .status(RoundStatus.BEFORE)
        .participantLimit(limit)
        .build();

    rounds.add(round);
  }

  public void start() {
    if (contestEventStatus != ContestEventStatus.READY) throw new IllegalStateException("이미 진행 중이거나 종료된 종목입니다");
    if (rounds.isEmpty()) throw new IllegalStateException("No round has been added");

    currentRound = rounds.get(0);
    currentRound.markAsInProgress();

    List<Participant> activeParticipants = participations.stream()
        .filter(p -> p.getStatus() == ParticipationStatus.ACTIVE)
        .map(Participation::getParticipant)
        .toList();

    currentRound.addParticipants(activeParticipants);
    contestEventStatus = ContestEventStatus.IN_PROGRESS;
    currentRound.start();
  }

  public Run getCurrentRun() {
    if (currentRound != null && currentRound.getCurrentRun() != null) return currentRound.getCurrentRun();
    throw new IllegalStateException("no currentRound or currentRun");
  }

  public void proceedRun() {
    if (contestEventStatus != ContestEventStatus.IN_PROGRESS) throw new IllegalStateException("시작하지 않았거나 종료된 종목입니다.");
    if (rounds.isEmpty()) throw new IllegalStateException("No round has been started");

    Run currentRun = this.getCurrentRun();

    int activeJudgesCount = this.judgings.stream()
        .filter(judging -> judging.getJudge().getStatus().equals(JudgeStatus.ACTIVE))
        .toList()
        .size();

    if (currentRun.canBeCompleted(activeJudgesCount)) {
      currentRun.markAsDone();
      Run nextRun = currentRound.findNextRun()
          .orElseThrow(() -> new IllegalStateException("해당 라운드의 마지막 참가자입니다."));

      currentRound.moveToRun(nextRun);
    }
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
