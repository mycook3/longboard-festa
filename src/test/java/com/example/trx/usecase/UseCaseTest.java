package com.example.trx.usecase;

import com.example.trx.domain.event.ContestEvent;
import com.example.trx.domain.event.DisciplineCode;
import com.example.trx.domain.event.Division;
import com.example.trx.domain.user.Gender;
import com.example.trx.domain.user.Participant;
import com.example.trx.domain.user.UserStatus;
import com.example.trx.repository.ContestEventRepository;
import com.example.trx.repository.ParticipantRepository;
import java.time.LocalDate;
import lombok.extern.slf4j.Slf4j;
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

  @Test
  @Transactional
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
  public void addParticipantAndJudge() {




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
