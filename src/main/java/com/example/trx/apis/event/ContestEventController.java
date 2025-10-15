package com.example.trx.apis.event;

import com.example.trx.apis.event.dto.CreateContestEventRequest;
import com.example.trx.service.event.ContestEventService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/contest")
@RequiredArgsConstructor
@Tag(name = "ContestEvents", description = "대회 진행 종목 관리 API")
public class ContestEventController {

  private final ContestEventService contestEventService;

  @PostMapping
  public void createContestEvent(@RequestBody CreateContestEventRequest createContestEventRequest) {


  }

  @PostMapping
  public void startContestEvent() {



  }

  @PostMapping
  public void proceedRun() {



  }

  @PostMapping
  public void proceedRound() {



  }

  @GetMapping
  public void getCurrentContestEvent() {



  }

  @PostMapping
  public void addRound() {



  }

  @PostMapping
  public void submitScore() {



  }
}

