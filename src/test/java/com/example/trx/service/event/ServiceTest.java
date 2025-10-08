package com.example.trx.service.event;

import static org.junit.jupiter.api.Assertions.*;

import com.example.trx.apis.judge.dto.JudgeCreateRequest;
import com.example.trx.domain.event.ContestEvent;
import com.example.trx.domain.event.ContestEventStatus;
import com.example.trx.domain.event.DisciplineCode;
import com.example.trx.domain.event.Division;
import com.example.trx.domain.event.exception.ContestEventAlreadyExistsException;
import com.example.trx.domain.user.Gender;
import com.example.trx.domain.user.Participant;
import com.example.trx.repository.event.ContestEventRepository;
import com.example.trx.repository.user.ParticipantRepository;
import com.example.trx.service.judge.JudgeService;
import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@SpringBootTest
class ServiceTest {

  @Autowired
  private ContestEventService contestEventService;

  @Autowired
  private JudgeService judgeService;

  @Autowired
  private ContestEventRepository contestEventRepository;

  @Autowired
  private ParticipantRepository participantRepository;


  @Test
  public void createEventTest() {
    contestEventService.createContestEvent("BEGINNER", "FREESTYLE");
    assertEquals(1, contestEventRepository.count());

    assertThrows(
        ContestEventAlreadyExistsException.class,
        () -> contestEventService.createContestEvent("BEGINNER", "FREESTYLE")
    );
  }

  @Test
  @Transactional
  public void addRoundTest() {
    ContestEvent event = contestEventService.createContestEvent("BEGINNER", "FREESTYLE");
    contestEventService.addRound(1L, "32강", 32);

    ContestEvent saved = contestEventRepository.findById(1L).orElse(null);
    assertNotNull(saved);

    assertEquals(1, saved.getRounds().size());
    assertEquals("32강", saved.getRounds().get(0).getName());
    assertEquals(32, saved.getRounds().get(0).getParticipantLimit());
  }

  @Test
  @Transactional
  public void addParticipantTest() {
    ContestEvent event = contestEventService.createContestEvent("BEGINNER", "FREESTYLE");
    contestEventService.addRound(1L, "32강", 32);

    Participant participant = Participant.builder()
        .nameKr("박영서")
        .bibNumber(1)
        .phone("010-0000-0000")
        .emergencyContact("010-1111-1111")
        .gender(Gender.MALE)
        .birth(LocalDate.of(1995, 6, 8))
        .email("pj0642@gmail.com")
        .division(Division.BEGINNER)
        .residence("서울특별시 관악구")
        .oneLiner("ㅎㅇㅎㅇ")
        .build();

    participant.participate(event);

    ContestEvent saved = contestEventRepository.findById(1L).orElse(null);
    assertEquals(1, saved.getParticipations().size());
  }

  @Test
  @Transactional
  public void startTest() {
    ContestEvent event = contestEventService.createContestEvent("BEGINNER", "FREESTYLE");
    contestEventService.addRound(1L, "32강", 32);

    /////////////////////////////////////////// TODO 참가자 추가 => 서비스 분리 필요
    Participant participant = Participant.builder()
        .nameKr("박영서")
        .bibNumber(1)
        .phone("010-0000-0000")
        .emergencyContact("010-1111-1111")
        .gender(Gender.MALE)
        .birth(LocalDate.of(1995, 6, 8))
        .email("pj0642@gmail.com")
        .division(Division.BEGINNER)
        .residence("서울특별시 관악구")
        .oneLiner("ㅎㅇㅎㅇ")
        .build();

    participantRepository.save(participant);
    participant.participate(event);
    ////////////////////////////////////////////////////////

    contestEventService.startContestEvent(1L);

    ContestEvent saved = contestEventRepository.findById(1L).orElse(null);
    assertEquals(ContestEventStatus.IN_PROGRESS, saved.getContestEventStatus());
    assertEquals(32, saved.getCurrentRound().getParticipantLimit());
    assertEquals("박영서", saved.getCurrentRun().getParticipant().getNameKr());
  }

  @Test
  public void addJudgeAndSubmitScoreTest() {
    ContestEvent event = contestEventService.createContestEvent("BEGINNER", "FREESTYLE");
    contestEventService.addRound(1L, "결승", 1);

    /////////////////////////////////////////// TODO 참가자 추가 => 서비스 분리 필요

    ///////////////////////////////////////////

    contestEventService.startContestEvent(1L);

    JudgeCreateRequest request = JudgeCreateRequest.builder()
        .judgeNumber(1)
        .name("김심사")
        .username("judge_kim")
        .password("1234")
        .disciplineCode(DisciplineCode.FREESTYLE)
        .build();

    judgeService.createJudge(request);

  }

  @Test
  public void proceedRunTest() {
      ContestEvent event = contestEventService.createContestEvent("BEGINNER", "FREESTYLE");
      contestEventService.addRound(1L, "결승", 2);

      Participant jihwan = Participant.builder()
        .nameKr("박지환")
        .bibNumber(2)
        .phone("010-0000-0000")
        .emergencyContact("010-1111-1111")
        .gender(Gender.MALE)
        .birth(LocalDate.of(1995, 11, 12))
        .email("mycook3@naver.com")
        .division(Division.BEGINNER)
        .residence("서울특별시 강동구")
        .oneLiner("ㅎㅇㅎㅇ2")
        .build();


    jihwan.participate(event);



  }
}