package com.example.trx.service.event;

import com.example.trx.domain.event.ContestEvent;
import com.example.trx.domain.event.DisciplineCode;
import com.example.trx.domain.event.Division;
import com.example.trx.domain.event.exception.ContestEventNotFound;
import com.example.trx.repository.event.ContestEventRepository;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ContestEventService {

  private final ContestEventRepository contestEventRepository;

  public ContestEvent getContestEventByDivisionAndDisciplineCode(String divisionName, String eventName) {
    Division division = Division.valueOf(divisionName);
    DisciplineCode disciplineCode = DisciplineCode.valueOf(eventName);

    return contestEventRepository
        .findContestEventByDivisionAndDisciplineCode(division, disciplineCode)
        .orElseThrow(() -> new ContestEventNotFound(division, disciplineCode));
  }

  @Transactional
  public ContestEvent createContestEvent(String divisionName, String eventName) {//TODO: 응답 DTO
    Division division = Division.valueOf(divisionName);
    DisciplineCode disciplineCode = DisciplineCode.valueOf(eventName);

    ContestEvent contestEvent = ContestEvent.builder()
        .division(division)
        .disciplineCode(disciplineCode)
        .build();

    return contestEventRepository.save(contestEvent);
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

  public void addParticipant() {




  }
}
