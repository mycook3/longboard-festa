package com.example.trx.domain.event.round;

import com.example.trx.domain.event.ContestEvent;
import com.example.trx.domain.event.RoundProgressionType;
import com.example.trx.domain.event.round.match.Match;
import com.example.trx.domain.event.round.match.MatchType;
import com.example.trx.domain.event.round.run.Run;
import com.example.trx.domain.event.round.run.RunStatus;
import com.example.trx.domain.judge.Judge;
import com.example.trx.domain.event.round.run.score.ScoreStatus;
import com.example.trx.domain.event.round.run.score.ScoreTotal;
import com.example.trx.domain.user.Participant;
import jakarta.persistence.CascadeType;
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
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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

  private String name;
  private Integer participantLimit;

  @Builder.Default
  private Integer runsPerParticipant = 1;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "current_run_id")
  private Run currentRun;

  @Enumerated(EnumType.STRING)
  @Builder.Default
  private RoundStatus status = RoundStatus.BEFORE;

  @OneToMany(mappedBy = "round", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @Builder.Default
  private List<Run> runs =  new ArrayList<>();

  @OneToMany(mappedBy = "round", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @Builder.Default
  private List<Match> matches =  new ArrayList<>();

  public void addParticipants(List<Participant> participants) {
    if (participants.size() > participantLimit) throw new IllegalArgumentException("참가자 제한 수 초과");
    RoundProgressionType progressionType = contestEvent.getProgressionType();

    switch (progressionType) {
      case TOURNAMENT -> addTournamentMatchesAndRuns(participants);
      case SCORE_BASED -> addScoreBasedRuns(participants);
    }
  }

  private void addScoreBasedRuns(List<Participant> participants) {
    for (int attempt = 1; attempt <= runsPerParticipant; attempt++) {
      for (Participant participant : participants) {
        Run run = Run.builder()
            .round(this)
            .attemptNumber(attempt)
            .participant(participant)
            .build();
        runs.add(run);
      }
    }
  }

  private void addTournamentMatchesAndRuns(List<Participant> participants) {
    for (int i = 0; i < participants.size(); i += 2) {
      Participant p1 = participants.get(i);
      Participant p2 = (i + 1 < participants.size() ? participants.get(i + 1) : null);

      Match match = Match.builder()
          .round(this)
          .matchType(p2 == null ? MatchType.BYE : MatchType.NORMAL)
          .build();

      matches.add(match);

      for (int attempt = 1; attempt <= runsPerParticipant; attempt++) {
        Run run1 = Run.builder()
              .round(this)
              .match(match)
              .participant(p1)
              .attemptNumber(i)
              .build();

        Run run2 = Run.builder()
              .round(this)
              .match(match)
              .participant(p2)
              .attemptNumber(i)
              .build();

        runs.add(run1);
        runs.add(run2);

        match.addRun(run1);
        match.addRun(run2);
      }
    }
  }

  public Optional<Run> findNextRun() {
   return runs.stream()
        .filter(run -> run.getStatus().equals(RunStatus.WAITING))
        .findFirst();
  }

  public void start(List<Judge> judges) {
    if (runs.isEmpty()) throw new IllegalStateException("no runs added");
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
    return this.currentRun == runs.get(runs.size() - 1) && this.currentRun.getStatus() == RunStatus.DONE;
  }

  public List<Participant> getSurvivors(Round nextRound) {
    RoundProgressionType progressionType = contestEvent.getProgressionType();

    return switch (progressionType) {
      case TOURNAMENT -> getTournamentWinners(nextRound);
      case SCORE_BASED -> getScoreBasedWinners(nextRound);
    };
  }

  private List<Participant> getTournamentWinners(Round nextRound) {
    List<Participant> tournamentWinners = new ArrayList<>();

    for (Match match: matches) {
      tournamentWinners.addAll(match.getWinners());
    }

    if (tournamentWinners.size() != nextRound.getParticipantLimit()) throw new IllegalStateException();//TODO
    return tournamentWinners;
  }

  private List<Participant> getScoreBasedWinners(Round nextRound) {
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
