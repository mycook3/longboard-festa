package com.example.trx.domain.event.round;

import com.example.trx.domain.event.RoundProgressionType;
import com.example.trx.domain.event.round.run.Run;
import com.example.trx.domain.event.round.run.RunStatus;
import com.example.trx.domain.event.round.run.score.ScoreTotal;
import com.example.trx.domain.judge.Judge;
import com.example.trx.domain.user.Participant;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Entity
@Data
@DiscriminatorValue("SCORE_BASED")
@AllArgsConstructor
@SuperBuilder
public class ScoreBasedRound extends Round {

  @Override
  public void addParticipants(List<Participant> participants) {
    if (participants.size() > participantLimit) {
      log.info("participants.size(): {}, limit: {}", participants.size(), participantLimit);
      throw new IllegalArgumentException("참가자 제한 수 초과. expected: " + participantLimit + "actual: " + participants.size());
    }

    for (Participant participant : participants) {
      for (int attempt = 1; attempt <= runsPerParticipant; attempt++) {
        Run run = Run.builder()
            .round(this)
            .attemptNumber(attempt)
            .participant(participant)
            .build();
        runs.add(run);
      }
    }
  }

  /**
   * 현재 시도를 다음으로 넘깁니다
   * 현재 active 상태의 심사위원 전원이 점수를 제출한 경우에만 넘길 수 있습니다
   * 라운드의 마지막 순서인 경우에는 예외를 던집니다
   */
  @Override
  public void proceed() {
    if (currentRun == null) throw new IllegalStateException("no currentRun set");

    if (currentRun.canBeCompleted()) {
      currentRun.markAsDone();
      Optional<Run> nextRun = findNextRun();
      nextRun.ifPresent(this::moveToRun);
    }
    else throw new IllegalStateException("일부 심사위원이 점수를 제출하지 않았습니다.");
  }

  private Optional<Run> findNextRun() {
   return runs.stream()
        .filter(run -> run.getStatus().equals(RunStatus.WAITING))
        .findFirst();
  }

  @Override
  public void start(List<Judge> judges) {
    if (runs.isEmpty()) throw new IllegalStateException("no runs added");
    status = RoundStatus.IN_PROGRESS;

    for (Run run: runs) {
        for (Judge judge: judges) {
          ScoreTotal emptyScoreSheet = ScoreTotal.builder()
              .judge(judge)
              .breakdownJson("")
              .total(BigDecimal.ZERO)
              .build();

          run.addScore(emptyScoreSheet);
        }
      }

      currentRun = runs.get(0);
      currentRun.markAsOngoing();
  }

  private void moveToRun(Run run) {
    if (run == null) throw new IllegalArgumentException("Run is null");
    currentRun = run;
    run.markAsOngoing();
  }

  @Override
  public boolean canBeCompleted() {
    return runs.stream().allMatch(run -> run.getStatus()==RunStatus.DONE);
  }

  @Override
  public List<Participant> getSurvivors(Round nextRound) {
    Map<Participant, BigDecimal> bestScores = runs.stream()
        .collect(Collectors.groupingBy(
            Run::getParticipant,
            Collectors.mapping(
                Run::getScore,
                Collectors.maxBy(Comparator.naturalOrder())
            )
        ))
        .entrySet().stream()
        .collect(Collectors.toMap(
            Map.Entry::getKey,
            entry -> entry.getValue().orElse(BigDecimal.ZERO)
        ));

    return bestScores.entrySet().stream()
        .sorted(Map.Entry.<Participant, BigDecimal> comparingByValue().reversed())
        .limit(nextRound.getParticipantLimit())
        .map(Map.Entry::getKey)
        .toList();
  }
}
