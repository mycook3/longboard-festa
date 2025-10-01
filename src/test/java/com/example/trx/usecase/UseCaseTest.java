package com.example.trx.usecase;

import com.example.trx.domain.event.ContestEvent;
import com.example.trx.domain.event.DisciplineCode;
import com.example.trx.domain.event.Division;
import com.example.trx.domain.judge.Judge;
import com.example.trx.domain.user.Gender;
import com.example.trx.domain.user.Participant;
import com.example.trx.domain.user.UserStatus;
import com.example.trx.repository.ContestEventRepository;
import com.example.trx.repository.JudgeRepository;
import com.example.trx.repository.ParticipantRepository;
import java.time.LocalDate;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
@EnableJpaAuditing
public class UseCaseTest {

  @Autowired
  private ContestEventRepository contestEventRepository;

  @Autowired
  private ParticipantRepository participantRepository;

  @Autowired
  private JudgeRepository judgeRepository;

  @Test
  @Order(1)
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

  @Test
  @Transactional
  @Order(2)
  public void findContestEventAndAddParticipant() {
    Participant participant = Participant.builder()
        .email("pj0642-gmail.com")
        .birth(LocalDate.of(1995, 6, 8))
        .memo("hello")
        .phone("010-4044-9987")
        .bibNumber(1)
        .emergencyContact("010-4044-9987")
        .gender(Gender.MALE)
        .nameKr("박영서")
        .oneLiner("안녕하세요")
        .residence("자택경비원")
        .division(Division.BEGINNER)
        .userStatus(UserStatus.WAITING)
        .build();

    participantRepository.save(participant);

    ContestEvent event = contestEventRepository.findContestEventByDivisionAndDisciplineCode(participant.getDivision(), DisciplineCode.FREESTYLE).orElse(null);

    assertNotNull(event);
    participant.participate(event);

    assertEquals(1,  participant.getParticipations().size());
    assertEquals(1, event.getRuns().size());
  }

  @Test
  @Transactional
  @Order(3)
  public void addParticipantAndJudge() {

    ContestEvent event = contestEventRepository.findContestEventByDivisionAndDisciplineCode(Division.BEGINNER, DisciplineCode.FREESTYLE).orElse(null);

    Judge judge = Judge.builder()
        .judgeNumber(event.getJudges().size() + 1)
        .name("김심사")
        .contestEvent(event)
        .build();

    judgeRepository.save(judge);
    event.addJudge(judge);
    assertEquals(1, judge.getJudgeNumber());
    assertEquals(1, judge.getContestEvent().getJudges().size());
  }

  @Test
  public void submitScore() {




  }

  @Test
  public void endRun() {




  }

  @Test
  public void endRound() {




  }
}
