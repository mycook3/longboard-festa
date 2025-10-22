package com.example.trx.domain.event.round.match;

import com.example.trx.domain.event.round.Round;
import com.example.trx.domain.event.round.run.Run;
import com.example.trx.domain.event.round.run.RunStatus;
import com.example.trx.domain.user.Participant;
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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Match {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  @Builder.Default
  private MatchStatus status = MatchStatus.WAITING;

  @ManyToOne(fetch = FetchType.LAZY)
  private Round round;

  @ManyToOne(fetch = FetchType.LAZY)
  private Participant participant1;

  @ManyToOne(fetch = FetchType.LAZY)
  private Participant participant2;

  @ManyToOne(fetch = FetchType.LAZY)
  private Participant winner;//동점자 처리가 필요한 경우 수동으로 정해야합니다.

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "current_run_id")
  private Run currentRun;

  @OneToMany (mappedBy = "match", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  @Builder.Default
  private List<Run> runs = new ArrayList<>();

  @Enumerated(EnumType.STRING)
  @Builder.Default
  private MatchType matchType = MatchType.NORMAL;

  public void start() {
     if (runs.isEmpty()) throw new IllegalStateException("no runs added");

     currentRun = runs.get(0);
     currentRun.markAsOngoing();
  }

  public void addRun(Run run) {
    run.setMatch(this);
    runs.add(run);
  }

   public Optional<Run> findNextRun() {
   return runs.stream()
        .filter(run -> run.getStatus().equals(RunStatus.WAITING))
        .findFirst();
  }

  private void moveToRun(Run run) {
    if (run == null) throw new IllegalArgumentException("Run is null");
    run.markAsOngoing();
    currentRun = run;
  }

  public void proceedRun() {
    if (currentRun.canBeCompleted()) {
      currentRun.markAsDone();
      Optional<Run> nextRun = findNextRun();

      if (nextRun.isPresent()) moveToRun(nextRun.get());
      else markAsDone();
    }
    else throw new IllegalStateException("일부 심사위원이 점수를 제출하지 않았습니다.");
  }

  public boolean canBeCompleted() {
    return runs.stream().allMatch(run -> run.getStatus() == RunStatus.DONE);
  }

  public boolean isCompleted() {
    return this.status == MatchStatus.DONE;
  }

  public void markAsOngoing() {
    this.status = MatchStatus.ONGOING;
  }

  public void markAsDone() {
    this.status = MatchStatus.DONE;
  }

  public void determineWinner() {
    if (winner != null) return;
    if (matchType == MatchType.BYE) return;

    BigDecimal bestScore1 = runs.stream()
        .filter(run -> run.getParticipant().equals(participant1))
        .map(Run::getScore)
        .max(Comparator.naturalOrder())
        .orElse(BigDecimal.ZERO);

    BigDecimal bestScore2 = runs.stream()
        .filter(run -> run.getParticipant().equals(participant2))
        .map(Run::getScore)
        .max(Comparator.naturalOrder())
        .orElse(BigDecimal.ZERO);

    int comparison = bestScore1.compareTo(bestScore2);

    if (comparison > 0) this.winner = participant1;
    else if (comparison < 0) this.winner = participant2;
    else throw new IllegalStateException("동점자 발생");
  }

  public List<Participant> getWinners() {
    if (matchType == MatchType.BYE) {//부전승 처리된 경우 모두를 다음 라운드로 진행시킵니다
      List<Participant> winners = new ArrayList<>();
      if (participant1 != null) winners.add(participant1);
      if (participant2 != null) winners.add(participant2);
      return winners;
    }

    if (winner == null) throw new IllegalStateException("아직 매치의 승자가 결정되지 않았습니다.");

    return List.of(winner);
  }

  public void markAsBye() {
    this.matchType = MatchType.BYE;
    this.status = MatchStatus.DONE;
  }

  public void setManualWinner(Participant winner) {
    if (!participant1.equals(winner) && !participant2.equals(winner)) {
      throw new IllegalArgumentException("해당 매치의 참가자가 아닙니다");
    }
    this.winner = winner;
    this.status = MatchStatus.DONE;
  }
}
