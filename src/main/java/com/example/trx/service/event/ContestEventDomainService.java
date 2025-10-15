package com.example.trx.service.event;

import com.example.trx.domain.event.ContestEvent;
import com.example.trx.domain.event.DisciplineCode;
import com.example.trx.domain.event.Division;
import com.example.trx.domain.event.exception.ContestEventAlreadyExistsException;
import com.example.trx.domain.event.exception.ContestEventNotFound;
import com.example.trx.domain.judge.Judge;
import com.example.trx.domain.judge.exception.JudgeNotFoundException;
import com.example.trx.domain.run.Run;
import com.example.trx.domain.run.exception.RunNotFoundException;
import com.example.trx.domain.score.ScoreTotal;
import com.example.trx.repository.event.ContestEventRepository;
import com.example.trx.repository.event.RoundRepository;
import com.example.trx.repository.judge.JudgeRepository;
import com.example.trx.repository.run.RunRepository;
import com.example.trx.repository.score.ScoreTotalRepository;
import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ContestEventDomainService {

  private final ContestEventRepository contestEventRepository;
  private final JudgeRepository judgeRepository;
  private final RunRepository runRepository;
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
  public ContestEvent createContestEvent(String divisionName, String eventName) {//NO NEED
    Division division = Division.valueOf(divisionName);
    DisciplineCode disciplineCode = DisciplineCode.valueOf(eventName);

    if (getContestEventByDivisionAndDisciplineCode(divisionName, eventName) != null) throw new ContestEventAlreadyExistsException(division, disciplineCode);

    ContestEvent contestEvent = ContestEvent.builder()
        .division(division)
        .disciplineCode(disciplineCode)
        .build();

    return contestEventRepository.save(contestEvent);
  }

  @Transactional
  public void addRound(Long eventId, String roundName, Integer limit) {
    ContestEvent contestEvent = getContestEventById(eventId);
    contestEvent.addRound(roundName, limit);
  }

  @Transactional
  public void initContest(Long eventId) {
    ContestEvent contestEvent = getContestEventById(eventId);
    contestEvent.init();
  }

  @Transactional
  public void startContestEvent(Long eventId) {
    ContestEvent contestEvent = getContestEventById(eventId);
    contestEvent.startFirstRound();
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
