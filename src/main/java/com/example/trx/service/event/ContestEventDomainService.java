package com.example.trx.service.event;

import com.example.trx.domain.event.ContestEvent;
import com.example.trx.domain.event.ContestEventStatus;
import com.example.trx.domain.event.DisciplineCode;
import com.example.trx.domain.event.Division;
import com.example.trx.domain.event.RoundProgressionType;
import com.example.trx.domain.event.round.Round;
import com.example.trx.domain.event.exception.ContestEventNotFound;
import com.example.trx.domain.event.round.RoundStatus;
import com.example.trx.domain.event.round.TournamentRound;
import com.example.trx.domain.event.round.match.Match;
import com.example.trx.domain.judge.Judge;
import com.example.trx.domain.judge.exception.JudgeNotFoundException;
import com.example.trx.domain.event.round.run.Run;
import com.example.trx.domain.event.round.run.exception.RunNotFoundException;
import com.example.trx.domain.event.round.run.score.ScoreTotal;
import com.example.trx.domain.user.Participant;
import com.example.trx.repository.event.ContestEventRepository;
import com.example.trx.repository.event.MatchRepository;
import com.example.trx.repository.event.RoundRepository;
import com.example.trx.repository.judge.JudgeRepository;
import com.example.trx.repository.event.RunRepository;
import com.example.trx.repository.event.ScoreTotalRepository;
import com.example.trx.repository.user.ParticipantRepository;
import java.math.BigDecimal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContestEventDomainService {

  private final ContestEventRepository contestEventRepository;
  private final JudgeRepository judgeRepository;
  private final RunRepository runRepository;
  private final RoundRepository roundRepository;
  private final MatchRepository matchRepository;
  private final ScoreTotalRepository scoreTotalRepository;
  private final ParticipantRepository participantRepository;

  public ContestEvent getContestEventByDivisionAndDisciplineCode(String divisionName, String eventName) {
    Division division = Division.valueOf(divisionName);
    DisciplineCode disciplineCode = DisciplineCode.valueOf(eventName);

    return contestEventRepository
        .findContestEventByDivisionAndDisciplineCode(division, disciplineCode)
        .orElse(null);
  }

  public ContestEvent getContestEventById(Long contestEventId) {
    return contestEventRepository
        .findById(contestEventId)
        .orElseThrow(() -> new ContestEventNotFound(contestEventId));
  }

  @Transactional
  public List<ContestEvent> getContestEventsRoundInProgress() {
    List<Round> rounds = roundRepository.findRoundsByStatus(RoundStatus.IN_PROGRESS);
    return rounds.stream().map(Round::getContestEvent).toList();
  }

  @Transactional
  public Round addRound(Long eventId, String roundName, Integer limit, Integer runPerParticipant) {
    ContestEvent contestEvent = getContestEventById(eventId);
    return contestEvent.addRound(roundName, limit, runPerParticipant);
  }

  @Transactional
  public void editRound(Long roundId, String roundName, Integer limit) {
    Round round = roundRepository.findById(roundId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 라운드 id입니다."));
    round.setName(roundName);
    round.setParticipantLimit(limit);
  }

  @Transactional
  public void deleteRound(Long roundId) {
    roundRepository.deleteById(roundId);
  }

  @Transactional
  public void initAll() {
    List<ContestEvent> contestEvents = contestEventRepository.findAll();

    for (ContestEvent contestEvent : contestEvents) {
      if (contestEvent.getContestEventStatus() != ContestEventStatus.NOT_INITIALIZED) continue;

      contestEvent.init();
       if (contestEvent.getProgressionType() == RoundProgressionType.TOURNAMENT) {
        setUpTournamentRound((TournamentRound) contestEvent.getCurrentRound());
      }
    }
  }

  public Boolean isContestInitialized() {
    Long initializedCount = contestEventRepository.countContestEventByContestEventStatusIsNot(ContestEventStatus.NOT_INITIALIZED);
    Long allCount = contestEventRepository.count();
    return initializedCount.equals(allCount);
  }


  @Transactional
  public void initContest(Long eventId) {
    ContestEvent contestEvent = getContestEventById(eventId);
    contestEvent.init();

    log.info("called");
    //의존성 문제로 인해 Match가 있다면 Match 저장 -> 이후 Run 저장을 강제해야 함
    if (contestEvent.getProgressionType() == RoundProgressionType.TOURNAMENT) {
      setUpTournamentRound((TournamentRound) contestEvent.getCurrentRound());
    }
  }

  private void setUpTournamentRound(TournamentRound round) {
    matchRepository.saveAll(round.getMatches());
    runRepository.saveAll(round.getRuns());
  }

  @Transactional
  public void startCurrentRound(Long eventId) {
    ContestEvent contestEvent = getContestEventById(eventId);
    List<Judge> activeJudges = judgeRepository.findAllByDeletedFalse();
    contestEvent.startCurrentRound(activeJudges);
  }

  @Transactional
  public void endContestEvent (Long eventId) {
    ContestEvent contestEvent = getContestEventById(eventId);
    contestEvent.end();
  }

  @Transactional
  public void proceedRunOrMatch(Long eventId) {
    ContestEvent contestEvent = getContestEventById(eventId);
    contestEvent.proceedRunOrMatch();
  }

  @Transactional
  public void proceedRound(Long eventId) {
    ContestEvent contestEvent = getContestEventById(eventId);
    contestEvent.proceedRound();

    if (contestEvent.getCurrentRound() instanceof TournamentRound && contestEvent.getContestEventStatus() == ContestEventStatus.READY) {
      setUpTournamentRound((TournamentRound) contestEvent.getCurrentRound());
    }
  }

  @Transactional
  public void submitScore(Long runId, Long judgeId, BigDecimal score, String breakdownJson) {
    Run run = runRepository.findById(runId).orElseThrow(() -> new RunNotFoundException(runId));
    Judge judge = judgeRepository.findById(judgeId).orElseThrow(() -> new JudgeNotFoundException(judgeId));
    judge.submitScore(run, score, breakdownJson);
  }

  @Transactional
  public void editScore(Long scoreId, BigDecimal newScore, String newBreakdownJson, String editedBy, String editReason) {
    ScoreTotal scoreTotal = scoreTotalRepository.findById(scoreId).orElseThrow(IllegalArgumentException::new);
    scoreTotal.setTotal(newScore);
    scoreTotal.setBreakdownJson(newBreakdownJson);
    scoreTotal.setEditedBy(editedBy);
    scoreTotal.setEditReason(editReason);
  }

  @Transactional
  public void makeMatchBye(Long matchId) {
    Match match = matchRepository.findById(matchId).orElseThrow(IllegalArgumentException::new);
    Round round = match.getRound();
    if (!(round instanceof TournamentRound tournamentRound)) {
      throw new IllegalStateException("토너먼트 라운드가 아닙니다");
    }
    else {
      tournamentRound.makeMatchBye(match);
    }
  }

  @Transactional
  public void makeManualParticipant(Long matchId, Long participantId) {
    Match match = matchRepository.findById(matchId).orElseThrow(IllegalArgumentException::new);
    Participant participant = participantRepository.findById(participantId).orElseThrow(IllegalArgumentException::new);

    Round round = match.getRound();
    if (!(round instanceof TournamentRound tournamentRound)) {
      throw new IllegalStateException("토너먼트 라운드가 아닙니다");
    }
    else {
      tournamentRound.setManualWinner(match, participant);
    }
  }

  @Transactional
  public void calculateMatchResult(Long matchId) {
    Match match = matchRepository.findById(matchId).orElseThrow(IllegalArgumentException::new);
    match.getWinner();
  }
}
