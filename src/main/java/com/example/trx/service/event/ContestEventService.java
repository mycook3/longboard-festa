package com.example.trx.service.event;

import com.example.trx.domain.event.ContestEvent;
import com.example.trx.domain.event.DisciplineCode;
import com.example.trx.domain.event.Division;
import com.example.trx.domain.event.Round;
import com.example.trx.domain.event.exception.ContestEventAlreadyExistsException;
import com.example.trx.domain.event.exception.ContestEventNotFound;
import com.example.trx.domain.judge.Judge;
import com.example.trx.repository.event.ContestEventRepository;
import com.example.trx.repository.event.RoundRepository;
import com.example.trx.repository.judge.JudgeRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ContestEventService {

  private final ContestEventRepository contestEventRepository;
  private final JudgeRepository judgeRepository;
  private final RoundRepository roundRepository;

  public ContestEvent getContestEventByDivisionAndDisciplineCode(String divisionName, String eventName) {
    Division division = Division.valueOf(divisionName);
    DisciplineCode disciplineCode = DisciplineCode.valueOf(eventName);

    return contestEventRepository
        .findContestEventByDivisionAndDisciplineCode(division, disciplineCode)
        .orElse(null);
  }

  @Transactional
  public ContestEvent createContestEvent(String divisionName, String eventName) {//TODO: 응답 DTO로 변경하기
    Division division = Division.valueOf(divisionName);
    DisciplineCode disciplineCode = DisciplineCode.valueOf(eventName);

    if (getContestEventByDivisionAndDisciplineCode(divisionName, eventName) != null) throw new ContestEventAlreadyExistsException(division, disciplineCode);

    ContestEvent contestEvent = ContestEvent.builder()
        .division(division)
        .disciplineCode(disciplineCode)
        .build();

    return contestEventRepository.save(contestEvent);
  }

  private ContestEvent getContestEventById(Long eventId) {
    return contestEventRepository.findById(eventId)
        .orElseThrow(() -> new ContestEventNotFound(eventId));
  }

  @Transactional
  public void addRound(Long eventId, String roundName, Integer limit) {
    ContestEvent contestEvent = getContestEventById(eventId);
    contestEvent.addRound(roundName, limit);
  }

  @Transactional
  public List<Round> deleteRound() {//TODO
    return null;
  }

  @Transactional
  public Round updateRound() { //TODO
    return null;
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


}
