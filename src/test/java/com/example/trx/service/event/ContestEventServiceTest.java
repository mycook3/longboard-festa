package com.example.trx.service.event;

import static org.junit.jupiter.api.Assertions.*;

import com.example.trx.domain.event.ContestEvent;
import com.example.trx.domain.event.ContestEventStatus;
import com.example.trx.domain.event.Division;
import com.example.trx.domain.event.Round;
import com.example.trx.domain.event.exception.ContestEventAlreadyExistsException;
import com.example.trx.domain.user.Gender;
import com.example.trx.domain.user.Participant;
import com.example.trx.domain.user.UserStatus;
import com.example.trx.repository.event.ContestEventRepository;
import java.time.LocalDate;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@SpringBootTest
class ContestEventServiceTest {

  @Autowired
  private ContestEventService contestEventService;

  @Autowired
  private ContestEventRepository contestEventRepository;


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

  }



}