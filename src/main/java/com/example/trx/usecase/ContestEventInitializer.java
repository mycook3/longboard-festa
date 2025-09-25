package com.example.trx.usecase;

import com.example.trx.domain.event.ContestEvent;
import com.example.trx.domain.event.DisciplineCode;
import com.example.trx.domain.event.Division;
import com.example.trx.repository.ContestEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

//TODO 패키지 위치 변경 필요
@Component
@RequiredArgsConstructor
public class ContestEventInitializer implements ApplicationRunner {

  private final ContestEventRepository contestEventRepository;

  @Override
  public void run(ApplicationArguments args) throws Exception {
    for (Division division : Division.values()) {
      for (DisciplineCode disciplineCode : DisciplineCode.values()) {
        ContestEvent contestEvent = contestEventRepository.findContestEventByDivisionAndDisciplineCode(division, disciplineCode).orElse(null);

        if (contestEvent == null) {
          contestEventRepository.save(new ContestEvent(division, disciplineCode));
        }
      }
    }
  }
}
