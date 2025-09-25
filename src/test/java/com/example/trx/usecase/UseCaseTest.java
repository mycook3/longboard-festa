package com.example.trx.usecase;

import com.example.trx.domain.event.ContestEvent;
import com.example.trx.domain.event.DisciplineCode;
import com.example.trx.domain.event.Division;
import com.example.trx.repository.ContestEventRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;

@Slf4j
@SpringBootTest
public class UseCaseTest {

  @Autowired
  private ContestEventRepository contestEventRepository;

  @Test
  public void initTest() {
    for (DisciplineCode disciplineCode : DisciplineCode.values()) {
      for (Division division : Division.values()) {
        ContestEvent contestEvent = contestEventRepository
            .findContestEventByDivisionAndDisciplineCode(division, disciplineCode)
            .orElse(null);

        assertNotNull(contestEvent);
      }
    }
  }



}
