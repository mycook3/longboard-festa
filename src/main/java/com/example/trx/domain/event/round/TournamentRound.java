package com.example.trx.domain.event.round;

import com.example.trx.domain.event.ContestEvent;
import com.example.trx.domain.event.RoundProgressionType;
import com.example.trx.domain.event.round.match.Match;
import com.example.trx.domain.event.round.match.MatchStatus;
import com.example.trx.domain.event.round.match.MatchType;
import com.example.trx.domain.event.round.run.Run;
import com.example.trx.domain.event.round.run.RunStatus;
import com.example.trx.domain.event.round.run.score.ScoreTotal;
import com.example.trx.domain.judge.Judge;
import com.example.trx.domain.user.Participant;
import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
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
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Entity
@DiscriminatorValue("TOURNAMENT")
@NoArgsConstructor
@SuperBuilder
@Data
public class TournamentRound extends Round {
  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "current_match_id")
  private Match currentMatch;

  @OneToMany(mappedBy = "round", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
  @Builder.Default
  private List<Match> matches =  new ArrayList<>();

  public void addParticipants(List<Participant> participants) {
    if (participants.size() > participantLimit) throw new IllegalArgumentException("참가자 제한 수 초과");

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
  @Override
  public void proceed() {
    if (currentMatch == null) throw new IllegalStateException("no currentMatch set");

    if (currentMatch.isCompleted()) {//현재 매치가 완료된 상태라면
      Optional<Match> nextMatch = findNextMatch();
      if (nextMatch.isPresent()) proceedMatch(nextMatch.get());
      else markAsCompleted();
    }
    else {
      proceedRun();
    }
  }

  private void proceedRun() {
    if (currentMatch == null) throw new IllegalStateException("현재 진행 중인 매치가 없습니다");
    currentMatch.proceedRun();
  }

  private void proceedMatch(Match nextMatch) {
    if (nextMatch == null) throw new IllegalArgumentException("다음 매치가 null입니다");
    if (!currentMatch.isCompleted()) throw new IllegalStateException("아직 시작하지 않았거나 진행 중인 매치입니다");

    currentMatch = nextMatch;
    moveToMatch(nextMatch);
  }

  public void makeMatchBye(Match match) {
    if (!matches.contains(match)) throw new IllegalArgumentException("이 라운드에 해당하는 매치가 아닙니다");
    match.markAsBye();
  }

  public void setManualWinner(Match match, Participant participant) {
    if (!matches.contains(match)) throw new IllegalArgumentException("이 라운드에 해당하는 매치가 아닙니다");
    match.setManualWinner(participant);
  }

  public Optional<Match> findNextMatch() {
   return matches.stream()
        .filter(match -> match.getStatus().equals(MatchStatus.WAITING))
        .findFirst();
  }

  private void moveToMatch(Match match) {
    if (match == null) throw new IllegalArgumentException("Run is null");
    currentMatch = match;
    currentMatch.start();
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

    currentMatch = matches.get(0);
    currentMatch.start();
  }

  @Override
  public boolean canBeCompleted() {
    return matches.stream().allMatch(match -> match.getStatus() == MatchStatus.DONE);
  }

  @Override
  public List<Participant> getSurvivors(Round nextRound) {
   List<Participant> tournamentWinners = new ArrayList<>();

    for (Match match: matches) {
      if (match.getMatchType() == MatchType.NORMAL && match.getWinner() == null) {
        match.determineWinner();
      }

      tournamentWinners.addAll(match.getWinners());
    }

    if (tournamentWinners.size() != nextRound.getParticipantLimit()) throw new IllegalStateException();//TODO
    return tournamentWinners;
  }
}
