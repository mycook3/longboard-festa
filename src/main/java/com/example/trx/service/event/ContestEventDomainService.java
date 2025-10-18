package com.example.trx.service.event;

import com.example.trx.domain.event.ContestEvent;
import com.example.trx.domain.event.DisciplineCode;
import com.example.trx.domain.event.Division;
import com.example.trx.domain.event.round.Round;
import com.example.trx.domain.event.exception.ContestEventNotFound;
import com.example.trx.domain.judge.Judge;
import com.example.trx.domain.judge.exception.JudgeNotFoundException;
import com.example.trx.domain.event.round.run.Run;
import com.example.trx.domain.event.round.run.exception.RunNotFoundException;
import com.example.trx.domain.event.round.run.score.ScoreTotal;
import com.example.trx.repository.event.ContestEventRepository;
import com.example.trx.repository.event.RoundRepository;
import com.example.trx.repository.judge.JudgeRepository;
import com.example.trx.repository.run.RunRepository;
import com.example.trx.repository.score.ScoreTotalRepository;
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
  private final ScoreTotalRepository scoreTotalRepository;

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
  public void initContest(Long eventId) {
    ContestEvent contestEvent = getContestEventById(eventId);
    contestEvent.init();
  }

  @Transactional
  public void startContestEvent(Long eventId) {
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
  public void proceedRun(Long eventId) {
    ContestEvent contestEvent = getContestEventById(eventId);
    int activeJudgesCount = judgeRepository.findAllByDeletedFalse().size();
    contestEvent.proceedRun(activeJudgesCount);
  }

  @Transactional
  public void proceedRound(Long eventId) {
    ContestEvent contestEvent = getContestEventById(eventId);
    contestEvent.proceedRound();
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
}
