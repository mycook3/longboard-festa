package com.example.trx.service.event;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.trx.apis.event.dto.response.ContestEventResponse;
import com.example.trx.apis.judge.dto.JudgeCreateRequest;
import com.example.trx.apis.user.dto.ParticipantCreateRequest;
import com.example.trx.domain.event.DisciplineCode;
import com.example.trx.domain.user.Participant;
import com.example.trx.repository.event.ContestEventRepository;
import com.example.trx.service.judge.JudgeService;
import com.example.trx.service.user.ParticipantService;
import com.example.trx.support.util.JsonUtil;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.support.TransactionTemplate;

@Slf4j
@SpringBootTest
@Deprecated
class ApplicationServiceTest {

  @Autowired
  private ContestEventApplicationService applicationService;

  @Autowired
  private ContestEventDomainService domainService;

  @Autowired
  private JudgeService judgeService;

  @Autowired
  private ContestEventRepository contestEventRepository;

  @Autowired
  private ParticipantService participantService;

  @Autowired
  private TransactionTemplate transactionTemplate;

  /**
  @Test
  public void startTest() {
    domainService.addRound(1L, "32강", 32, 1);
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

    applicationService.initContest(1L);
    applicationService.startContestEvent(1L);

    ContestEventResponse resp = applicationService.getContestEventById(1L);
    assertNotNull(resp);
    assertEquals("32강", resp.getCurrentRound());
  }

  @Test
  public void addJudgeAndSubmitScoreTest() {
    domainService.addRound(1L, "결승", 1, 1);

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

    applicationService.initContest(1L);
    applicationService.startContestEvent(1L);

    JudgeCreateRequest judgeCreateReq = JudgeCreateRequest.builder()
        .name("김심사")
        .username("judge_kim")
        .password("1234")
        .disciplineCode(DisciplineCode.FREESTYLE)
        .build();

    judgeService.createJudge(judgeCreateReq);
    domainService.submitScore(1L, 1L, BigDecimal.valueOf(100), "어쩌고저쩌고");

    ContestEventResponse resp = applicationService.getContestEventById(1L);
    log.info("current contestEvent: {}", JsonUtil.toJsonString(resp));
  }

  @Test
  public void proceedRunTest() {
    domainService.addRound(1L, "결승", 2, 1);

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

    domainService.initContest(1L);
    domainService.startContestEvent(1L);

    JudgeCreateRequest judgeCreateReq = JudgeCreateRequest.builder()
        .name("김심사")
        .username("judge_kim")
        .password("1234")
        .disciplineCode(DisciplineCode.FREESTYLE)
        .build();

    judgeService.createJudge(judgeCreateReq);
    domainService.submitScore(1L, 1L, BigDecimal.valueOf(100), "어쩌고저쩌고");
    domainService.proceedRun(1L);

    ContestEventResponse resp = applicationService.getContestEventById(1L);
    log.info("current contestEvent: {}", JsonUtil.toJsonString(resp));
  }

  @Test
  public void proceedRoundTest() {
    domainService.addRound(1L, "결승", 2, 1);
    domainService.addRound(1L, "우승", 1, 1);

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

    applicationService.initContest(1L);
    applicationService.startContestEvent(1L);

    JudgeCreateRequest judgeCreateReq = JudgeCreateRequest.builder()
        .name("김심사")
        .username("judge_kim")
        .password("1234")
        .disciplineCode(DisciplineCode.FREESTYLE)
        .build();

    judgeService.createJudge(judgeCreateReq);

    domainService.submitScore(1L, 1L, BigDecimal.valueOf(99), "어쩌고저쩌고");
    domainService.proceedRun(1L);

    domainService.submitScore(2L, 1L, BigDecimal.valueOf(100), "어쩌고저쩌고");

    domainService.proceedRun(1L);
    domainService.proceedRound(1L);

    ContestEventResponse resp = applicationService.getContestEventById(1L);
    log.info("current contestEvent: {}", JsonUtil.toJsonString(resp));
  }
  */
}
