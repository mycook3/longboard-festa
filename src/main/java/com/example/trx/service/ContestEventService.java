package com.example.trx.service;

import com.example.trx.domain.event.ContestEvent;
import com.example.trx.domain.event.DisciplineCode;
import com.example.trx.domain.event.Division;
import com.example.trx.repository.ContestEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ContestEventService {

  private final ContestEventRepository contestEventRepository;

  // Division * DisciplineCode 만큼의 종목 정보를 미리 생성
  public void init() {
    for (Division division : Division.values()) {
      for (DisciplineCode disciplineCode : DisciplineCode.values()) {
        ContestEvent contestEvent = contestEventRepository.findContestEventByDivisionAndDisciplineCode(division, disciplineCode).orElse(null);

        if (contestEvent == null) {
          contestEventRepository.save(new ContestEvent(division, disciplineCode));
        }
      }
    }
  }

  //종목별 참가자 추가
  public void addParticipant() {




  }

  public void addJudge() {




  }
}
