package com.example.trx.domain.event.round;

import com.example.trx.domain.event.ContestEvent;
import com.example.trx.domain.event.RoundProgressionType;
import com.example.trx.domain.event.round.match.Match;
import com.example.trx.domain.event.round.match.MatchStatus;
import com.example.trx.domain.event.round.match.MatchType;
import com.example.trx.domain.event.round.run.Run;
import com.example.trx.domain.event.round.run.RunStatus;
import com.example.trx.domain.judge.Judge;
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

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "current_match_id")
  private Match currentMatch;

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
    if (participants.size() > participantLimit) {
      log.info("participants.size(): {}, limit: {}", participants.size(), participantLimit);
      throw new IllegalArgumentException("참가자 제한 수 초과");
    }
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
          .participant1(p1)
          .participant2(p2)
          .matchType(p2 == null ? MatchType.BYE : MatchType.NORMAL)//짝을 맞출 수 없는 경우 부전승 처리
          .build();

      matches.add(match);

      if (match.getMatchType() == MatchType.NORMAL) {//부전승이 아니라면 == p2가 null이 아니라면
        for (int attempt = 1; attempt <= runsPerParticipant; attempt++) {//라운드 별 시도 제한만큼 Run 만들기
          Run run1 = Run.builder()
                .round(this)
                .match(match)
                .participant(p1)
                .attemptNumber(attempt)
                .build();

          Run run2 = Run.builder()
                .round(this)
                .match(match)
                .participant(p2)
                .attemptNumber(attempt)
                .build();

          runs.add(run1);
          runs.add(run2);

          match.addRun(run1);
          match.addRun(run2);
        }
      }
    }
  }

    /**
   * 현재 시도를 다음으로 넘깁니다
   * 현재 active 상태의 심사위원 전원이 점수를 제출한 경우에만 넘길 수 있습니다
   * 라운드의 마지막 순서인 경우에는 예외를 던집니다
   */
  public void proceedRunOrMatch(int activeJudgesCount) {
    RoundProgressionType progressionType = contestEvent.getProgressionType();

    switch (progressionType) {
      case SCORE_BASED -> proceedScoreBasedRun(activeJudgesCount);
      case TOURNAMENT -> proceedTournamentMatchAndRun(activeJudgesCount);
    }
  }

  private void proceedScoreBasedRun(int activeJudgesCount) {
    if (currentRun == null) throw new IllegalStateException("no currentRun set");

    if (currentRun.canBeCompleted(activeJudgesCount)) {
      currentRun.markAsDone();
      Run nextRun = findNextRun()
          .orElseThrow(() -> new IllegalStateException("해당 라운드의 마지막 시도입니다."));
      moveToRun(nextRun);
    }
    else throw new IllegalStateException("일부 심사위원이 점수를 제출하지 않았습니다.");
  }

  private void proceedTournamentMatchAndRun(int activeJudgesCount) {
    if (currentMatch == null) throw new IllegalStateException("no currentMatch set");

    if (currentMatch.canBeCompleted()) {
      currentMatch.markAsDone();

      Match nextMatch = findNextMatch()
          .orElseThrow(() -> new IllegalStateException("해당 라운드의 마지막 매치입니다."));

      moveToMatch(nextMatch);
    }
    else {
      currentMatch.proceedRun(activeJudgesCount);
    }
  }

  public void makeMatchBye(Match match) {
    if (!matches.contains(match)) throw new IllegalArgumentException("이 라운드에 해당하는 매치가 아닙니다");
    match.markAsBye();
  }

  public void setManualWinner(Match match, Participant participant) {
    if (!matches.contains(match)) throw new IllegalArgumentException("이 라운드에 해당하는 매치가 아닙니다");
    match.setManualWinner(participant);
  }

  public Optional<Run> findNextRun() {
   return runs.stream()
        .filter(run -> run.getStatus().equals(RunStatus.WAITING))
        .findFirst();
  }

  public Optional<Match> findNextMatch() {
   return matches.stream()
        .filter(match -> match.getStatus().equals(MatchStatus.WAITING))
        .findFirst();
  }

  public void start(List<Judge> judges) {
    if (runs.isEmpty()) throw new IllegalStateException("no runs added");
    RoundProgressionType progressionType = contestEvent.getProgressionType();

    switch (progressionType) {
      case SCORE_BASED -> initScoreBasedRuns(judges);
      case TOURNAMENT -> initTournamentMatches(judges);
    }
  }

  private void initScoreBasedRuns(List<Judge> judges) {
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

  private void initTournamentMatches(List<Judge> judges) {
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

    currentMatch = matches.get(0);
    currentMatch.markAsOngoing();
    currentMatch.start();
  }

  public void moveToRun(Run run) {
    if (run == null) throw new IllegalArgumentException("Run is null");
    currentRun = run;
    run.markAsOngoing();
  }

  public void moveToMatch(Match match) {
    if (match == null) throw new IllegalArgumentException("Run is null");
    currentMatch = match;
    match.markAsOngoing();
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
