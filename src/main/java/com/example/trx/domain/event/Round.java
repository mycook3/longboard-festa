package com.example.trx.domain.event;

import com.example.trx.domain.run.Run;
import com.example.trx.domain.score.ScoreTotal;
import com.example.trx.domain.user.Participant;
import com.example.trx.domain.user.UserStatus;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.math.BigDecimal;
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
public class Round {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  private ContestEvent contestEvent;

  @OneToOne(fetch = FetchType.LAZY)
  private Run currentRun;

  @Enumerated(EnumType.STRING)
  private RoundStatus status;

  @OneToMany
  private List<Run> runs;

  private String name;
  private Integer limit;

  public void addParticipants(List<Participant> participants) {
    for (Participant participant : participants) {
      Run run = Run.builder()
          .contestEvent(contestEvent)
          .participant(participant)
          .userStatus(UserStatus.WAITING)
          .build();
      runs.add(run);
    }
  }

  public Optional<Run> findNextRun() {
   return runs.stream()
        .filter(run -> run.getUserStatus().equals(UserStatus.WAITING))
        .findFirst();
  }

  public void moveToRun(Run run) {
    if (run == null) throw new IllegalArgumentException("Run is null");
    run.markAsOngoing();
    currentRun = run;
  }

  public void markAsInProgress() {
    this.status = RoundStatus.IN_PROGRESS;
  }

  public void markAsCompleted() {
    this.status = RoundStatus.COMPLETED;
  }

  public boolean canBeCompleted() {
    return this.currentRun == runs.get(runs.size() - 1) && this.currentRun.getUserStatus() == UserStatus.DONE;
  }

  public List<Participant> getSurvivors(Round nextRound) {
    return runs.stream()
        .sorted(
            Comparator.comparing((Run run) -> {
              List<BigDecimal> scores = run.getScores()
                  .stream()
                  .map(ScoreTotal::getTotal)
                  .sorted()
                  .toList();

              return scores.subList(1, scores.size() - 1)
                  .stream()
                  .reduce(BigDecimal.ZERO, BigDecimal::add);
            }).reversed()
        )
        .limit(nextRound.getLimit())
        .map(Run::getParticipant)
        .toList();
  }
}
