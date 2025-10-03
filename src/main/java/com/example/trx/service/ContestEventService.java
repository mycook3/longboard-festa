package com.example.trx.service;

import com.example.trx.domain.event.ContestEvent;
import com.example.trx.domain.event.DisciplineCode;
import com.example.trx.domain.event.Division;
import com.example.trx.repository.ContestEventRepository;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ContestEventService {

  private final ContestEventRepository contestEventRepository;

  public ContestEvent getContestEventByDivisionAndDisciplineCode(Division division, DisciplineCode disciplineCode) throws NoSuchElementException {
    return contestEventRepository
        .findContestEventByDivisionAndDisciplineCode(division, disciplineCode)
        .orElseThrow(() -> new NoSuchElementException("Contest event not found"));
  }

  @Transactional
  public void proceedRunOrRound(Division division, DisciplineCode disciplineCode) throws NoSuchElementException, IllegalStateException {
    ContestEvent contestEvent = getContestEventByDivisionAndDisciplineCode(division, disciplineCode);

    try {
      contestEvent.proceedRun();
    } catch (IllegalStateException e) {
      contestEvent.proceedRoundAndDropParticipants();
    }
  }


  //종목별 참가자 추가
  public void addParticipant() {




  }

  public void addJudge() {




  }
}
