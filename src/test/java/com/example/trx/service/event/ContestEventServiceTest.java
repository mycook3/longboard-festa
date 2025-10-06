package com.example.trx.service.event;

import static org.junit.jupiter.api.Assertions.*;

import com.example.trx.domain.event.ContestEvent;
import com.example.trx.domain.event.exception.ContestEventAlreadyExistsException;
import com.example.trx.repository.event.ContestEventRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
  public void addRoundTest() {
    ContestEvent event = contestEventService.createContestEvent("BEGINNER", "FREESTYLE");



  }

  @Test
  public void addParticipantTest() {



  }

  @Test
  public void startTest() {



  }



}