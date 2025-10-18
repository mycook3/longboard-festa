package com.example.trx.service.event;

import static org.junit.jupiter.api.Assertions.*;

import com.example.trx.apis.judge.dto.JudgeCreateRequest;
import com.example.trx.apis.user.dto.ParticipantCreateRequest;
import com.example.trx.domain.event.ContestEvent;
import com.example.trx.domain.event.ContestEventStatus;
import com.example.trx.domain.event.DisciplineCode;
import com.example.trx.domain.user.Participant;
import com.example.trx.repository.event.ContestEventRepository;
import com.example.trx.service.judge.JudgeService;
import com.example.trx.service.user.ParticipantService;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

@Slf4j
@SpringBootTest
class DomainServiceTest {

  @Autowired
  private ContestEventDomainService contestEventDomainService;

  @Autowired
  private JudgeService judgeService;

  @Autowired
  private ContestEventRepository contestEventRepository;

  @Autowired
  private ParticipantService participantService;

  @Autowired
  private TransactionTemplate transactionTemplate;

  @Test
  @Transactional
  public void addRoundTest() {
    contestEventDomainService.addRound(1L, "32강", 32);

    ContestEvent saved = contestEventRepository.findById(1L).orElse(null);
    assertNotNull(saved);

    assertEquals(1, saved.getRounds().size());
    assertEquals("32강", saved.getRounds().get(0).getName());
    assertEquals(32, saved.getRounds().get(0).getParticipantLimit());
  }

  @Test
  public void deleteRoundTest() {
    contestEventDomainService.addRound(1L, "32강", 32);
    contestEventDomainService.deleteRound(1L);

    ContestEvent saved = transactionTemplate.execute(status -> {
      ContestEvent ev = contestEventRepository.findById(1L).orElse(null);
      ev.getRounds().size();
      return ev;
    });

    assertEquals(0, saved.getRounds().size());
  }

  @Test
  public void editRoundTest() {
    contestEventDomainService.addRound(1L, "32강", 32);
    contestEventDomainService.editRound(1L, "16강", 16);

    ContestEvent saved = transactionTemplate.execute(status -> {
      ContestEvent ev = contestEventRepository.findById(1L).orElse(null);
      ev.getRounds().get(0);
      return ev;
    });

    assertEquals("16강", saved.getRounds().get(0).getName());
  }

  @Test
  @Transactional
  public void addParticipantTest() {
    contestEventDomainService.addRound(1L, "32강", 32);

    ParticipantCreateRequest request = ParticipantCreateRequest.builder()
        .nameKr("박영서")
        .phone("010-0000-0000")
        .emergencyContact("010-1111-1111")
        .gender("MALE")
        .birth(LocalDate.of(1995, 6, 8))
        .email("pj0642@gmail.com")
        .division("BEGINNER")
        .residence("서울특별시 관악구")
        .eventToParticipate(List.of("FREESTYLE"))
        .oneLiner("ㅎㅇㅎㅇ")
        .build();

    Participant participant = participantService.createParticipantAndParticipate(request);

    ContestEvent saved = contestEventRepository.findById(1L).orElse(null);
    assertEquals(1, saved.getParticipations().size());
  }

  @Test
  @Transactional
  public void startTest() {
    contestEventDomainService.addRound(1L, "32강", 32);

    ///////////////////////////////////////////
    ParticipantCreateRequest request = ParticipantCreateRequest.builder()
        .nameKr("박영서")
        .phone("010-0000-0000")
        .emergencyContact("010-1111-1111")
        .gender("MALE")
        .birth(LocalDate.of(1995, 6, 8))
        .email("pj0642@gmail.com")
        .division("BEGINNER")
        .residence("서울특별시 관악구")
        .eventToParticipate(List.of("FREESTYLE"))
        .oneLiner("ㅎㅇㅎㅇ")
        .build();

    Participant participant = participantService.createParticipantAndParticipate(request);
    ////////////////////////////////////////////////////////

    contestEventDomainService.initContest(1L);
    contestEventDomainService.startContestEvent(1L);

    ContestEvent saved = contestEventRepository.findById(1L).orElse(null);
    assertEquals(ContestEventStatus.IN_PROGRESS, saved.getContestEventStatus());
    assertEquals(32, saved.getCurrentRound().getParticipantLimit());
    assertEquals("박영서", saved.getCurrentRun().getParticipant().getNameKr());
  }

  @Test
  public void addJudgeAndSubmitScoreTest() {
    contestEventDomainService.addRound(1L, "결승", 1);

    ///////////////////////////////////////////
    ParticipantCreateRequest request = ParticipantCreateRequest.builder()
        .nameKr("박영서")
        .phone("010-0000-0000")
        .emergencyContact("010-1111-1111")
        .gender("MALE")
        .birth(LocalDate.of(1995, 6, 8))
        .email("pj0642@gmail.com")
        .division("BEGINNER")
        .residence("서울특별시 관악구")
        .eventToParticipate(List.of("FREESTYLE"))
        .oneLiner("ㅎㅇㅎㅇ")
        .build();

    Participant participant = participantService.createParticipantAndParticipate(request);
    ///////////////////////////////////////////

    contestEventDomainService.initContest(1L);
    contestEventDomainService.startContestEvent(1L);

    JudgeCreateRequest judgeCreateReq = JudgeCreateRequest.builder()
        .judgeNumber(1)
        .name("김심사")
        .username("judge_kim")
        .password("1234")
        .disciplineCode(DisciplineCode.FREESTYLE)
        .build();

    judgeService.createJudge(judgeCreateReq);
    contestEventDomainService.submitScore(1L, 1L, BigDecimal.valueOf(100), "어쩌고저쩌고");

    ContestEvent saved = transactionTemplate.execute(status -> {
      ContestEvent ev = contestEventRepository.findById(1L).orElse(null);
      ev.getCurrentRound().getRuns().size();
      ev.getCurrentRun().getScores().get(0);
      return ev;
    });

    assertTrue(BigDecimal.valueOf(100).compareTo(saved.getCurrentRun().getScores().get(0).getTotal()) == 0);
    assertEquals(1, saved.getCurrentRound().getRuns().size());
  }

  @Test
  public void proceedRunTest() {
    contestEventDomainService.addRound(1L, "결승", 2);

    ///////////////////////////////////////////
    ParticipantCreateRequest req1 = ParticipantCreateRequest.builder()
        .nameKr("박영서")
        .phone("010-0000-0000")
        .emergencyContact("010-1111-1111")
        .gender("MALE")
        .birth(LocalDate.of(1995, 6, 8))
        .email("pj0642@gmail.com")
        .division("BEGINNER")
        .residence("서울특별시 관악구")
        .eventToParticipate(List.of("FREESTYLE"))
        .oneLiner("ㅎㅇㅎㅇ")
        .build();

    ParticipantCreateRequest req2= ParticipantCreateRequest.builder()
        .nameKr("박영서2")
        .phone("010-0000-0000")
        .emergencyContact("010-1111-1111")
        .gender("MALE")
        .birth(LocalDate.of(1995, 6, 8))
        .email("pj0642@gmail.com")
        .division("BEGINNER")
        .residence("서울특별시 관악구")
        .eventToParticipate(List.of("FREESTYLE"))
        .oneLiner("ㅎㅇㅎㅇ")
        .build();

    participantService.createParticipantAndParticipate(req1);
    participantService.createParticipantAndParticipate(req2);
    ///////////////////////////////////////////

    contestEventDomainService.initContest(1L);
    contestEventDomainService.startContestEvent(1L);

    JudgeCreateRequest judgeCreateReq = JudgeCreateRequest.builder()
        .judgeNumber(1)
        .name("김심사")
        .username("judge_kim")
        .password("1234")
        .disciplineCode(DisciplineCode.FREESTYLE)
        .build();

    judgeService.createJudge(judgeCreateReq);
    contestEventDomainService.submitScore(1L, 1L, BigDecimal.valueOf(100), "어쩌고저쩌고");

    contestEventDomainService.proceedRun(1L);

    ContestEvent saved = transactionTemplate.execute(status -> {
      ContestEvent ev = contestEventRepository.findById(1L).orElse(null);
      ev.getCurrentRound().getRuns().size();
      ev.getCurrentRun();
      ev.getCurrentRun().getParticipant().getNameKr();
      return ev;
    });

    assertEquals(2, saved.getCurrentRound().getRuns().size());
    assertEquals(2L, saved.getCurrentRun().getId());
    assertEquals("박영서2", saved.getCurrentRun().getParticipant().getNameKr());
  }

  @Test
  public void proceedRoundTest() {
    contestEventDomainService.addRound(1L, "결승", 2);
    contestEventDomainService.addRound(1L, "우승", 1);

    ///////////////////////////////////////////
    ParticipantCreateRequest req1 = ParticipantCreateRequest.builder()
        .nameKr("박영서")
        .phone("010-0000-0000")
        .emergencyContact("010-1111-1111")
        .gender("MALE")
        .birth(LocalDate.of(1995, 6, 8))
        .email("pj0642@gmail.com")
        .division("BEGINNER")
        .residence("서울특별시 관악구")
        .eventToParticipate(List.of("FREESTYLE"))
        .oneLiner("ㅎㅇㅎㅇ")
        .build();

    ParticipantCreateRequest req2= ParticipantCreateRequest.builder()
        .nameKr("박영서2")
        .phone("010-0000-0000")
        .emergencyContact("010-1111-1111")
        .gender("MALE")
        .birth(LocalDate.of(1995, 6, 8))
        .email("pj0642@gmail.com")
        .division("BEGINNER")
        .residence("서울특별시 관악구")
        .eventToParticipate(List.of("FREESTYLE"))
        .oneLiner("ㅎㅇㅎㅇ")
        .build();

    participantService.createParticipantAndParticipate(req1);
    participantService.createParticipantAndParticipate(req2);
    ///////////////////////////////////////////

    contestEventDomainService.initContest(1L);
    contestEventDomainService.startContestEvent(1L);

    JudgeCreateRequest judgeCreateReq = JudgeCreateRequest.builder()
        .judgeNumber(1)
        .name("김심사")
        .username("judge_kim")
        .password("1234")
        .disciplineCode(DisciplineCode.FREESTYLE)
        .build();

    judgeService.createJudge(judgeCreateReq);

    contestEventDomainService.submitScore(1L, 1L, BigDecimal.valueOf(99), "어쩌고저쩌고");
    contestEventDomainService.proceedRun(1L);

    contestEventDomainService.submitScore(2L, 1L, BigDecimal.valueOf(100), "어쩌고저쩌고");

    contestEventDomainService.proceedRun(1L);
    contestEventDomainService.proceedRound(1L);

    ContestEvent saved = transactionTemplate.execute(status -> {
      ContestEvent ev = contestEventRepository.findById(1L).orElse(null);
      ev.getCurrentRound().getRuns();
      ev.getCurrentRound().getCurrentRun().getParticipant().getNameKr();

      return ev;
    });
    assertEquals("우승", saved.getCurrentRound().getName());
    assertEquals("박영서2", saved.getCurrentRound().getCurrentRun().getParticipant().getNameKr());
  }

}