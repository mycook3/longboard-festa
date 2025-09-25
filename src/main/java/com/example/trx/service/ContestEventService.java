package com.example.trx.service;

import com.example.trx.domain.event.ContestEvent;
import com.example.trx.domain.event.DisciplineCode;
import com.example.trx.domain.event.Division;
import com.example.trx.repository.ContestEventRepository;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContestEventService {

  private final ContestEventRepository contestEventRepository;

  public ContestEvent getContestEventByDivisionAndDisciplineCode(Division division, DisciplineCode disciplineCode) throws NoSuchElementException {
    return contestEventRepository
        .findContestEventByDivisionAndDisciplineCode(division, disciplineCode)
        .orElseThrow(() -> new NoSuchElementException("Contest event not found"));
  }

  //종목별 참가자 추가
  public void addParticipant() {




  }

  public void addJudge() {




  }
}
