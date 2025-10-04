package com.example.trx.service.event;

import com.example.trx.domain.event.ContestEvent;
import com.example.trx.domain.event.DisciplineCode;
import com.example.trx.domain.event.Division;
import com.example.trx.domain.event.Round;
import com.example.trx.domain.event.exception.ContestEventNotFound;
import com.example.trx.domain.user.Participant;
import com.example.trx.domain.user.Participation;
import com.example.trx.domain.user.ParticipationStatus;
import com.example.trx.repository.event.ContestEventRepository;
import com.example.trx.repository.user.ParticipationRepository;
import java.util.List;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ContestEventService {

  private final ContestEventRepository contestEventRepository;
  private final ParticipationRepository participationRepository;

  public ContestEvent getContestEventByDivisionAndDisciplineCode(String divisionName, String eventName) {
    Division division = Division.valueOf(divisionName);
    DisciplineCode disciplineCode = DisciplineCode.valueOf(eventName);

    return contestEventRepository
        .findContestEventByDivisionAndDisciplineCode(division, disciplineCode)
        .orElseThrow(() -> new ContestEventNotFound(division, disciplineCode));
  }

  @Transactional
  public ContestEvent createContestEvent(String divisionName, String eventName) {//TODO: 응답 DTO로 변경하기
    Division division = Division.valueOf(divisionName);
    DisciplineCode disciplineCode = DisciplineCode.valueOf(eventName);

    ContestEvent contestEvent = ContestEvent.builder()
        .division(division)
        .disciplineCode(disciplineCode)
        .build();

    return contestEventRepository.save(contestEvent);
  }

  @Transactional
  public void setRound(Long eventId, String roundName) {
    ContestEvent contestEvent = contestEventRepository.findById(eventId)
        .orElseThrow(() -> new ContestEventNotFound(eventId));

    Round round = Round.valueOf(roundName);
    contestEvent.setRound(round);
  }

  @Transactional
  public void addRuns(Long eventId) {
    ContestEvent contestEvent = contestEventRepository.findById(eventId)
        .orElseThrow(() -> new ContestEventNotFound(eventId));

    List<Participant> activeParticipants = participationRepository.findByContestEventAndStatus(contestEvent, ParticipationStatus.ACTIVE)
        .stream()
        .map(Participation::getParticipant)
        .toList();

    for (Participant participant : activeParticipants) {
      contestEvent.addRun(participant);
    }
  }

  @Transactional
  public void proceedRunOrRound(String divisionName, String eventName) {
    Division division = Division.valueOf(divisionName);
    DisciplineCode disciplineCode = DisciplineCode.valueOf(eventName);

    ContestEvent contestEvent = contestEventRepository
        .findContestEventByDivisionAndDisciplineCode(division, disciplineCode)
        .orElseThrow(() -> new ContestEventNotFound(division, disciplineCode));

    try {
      contestEvent.proceedRun();
    } catch (IllegalStateException e) {
      contestEvent.proceedRoundAndDropParticipants();
    }
  }
}
