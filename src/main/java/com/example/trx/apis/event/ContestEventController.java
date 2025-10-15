package com.example.trx.apis.event;

import com.example.trx.apis.event.dto.AddRoundRequest;
import com.example.trx.apis.event.dto.CreateContestEventRequest;
import com.example.trx.apis.event.dto.SubmitScoreRequest;
import com.example.trx.service.event.ContestEventService;
import com.example.trx.service.judge.JudgeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/contest")
@RequiredArgsConstructor
@Tag(name = "ContestEvents", description = "대회 진행 종목 관리 API")
public class ContestEventController {

  private final ContestEventService contestEventService;
  private final JudgeService judgeService;

  @Operation(summary = "대회 종목 생성", description = "수준 및 종목명을 통해 대회 종목을 생성합니다.")
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  @PreAuthorize("hasRole('ADMIN')")
  public void createContestEvent(@Valid @RequestBody CreateContestEventRequest request) {
    contestEventService.createContestEvent(request.getDivision(),  request.getDisciplineCode());
  }

  @Operation(summary = "종목 시작", description = "선택된 종목을 시작합니다")
  @ResponseStatus(HttpStatus.ACCEPTED)
  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping("/{id}")
  public void startContestEvent(@PathVariable Long id) {
    contestEventService.startContestEvent(id);
  }

  @Operation(summary = "라운드 넘기기", description = "선택된 종목의 라운드를 다음으로 넘깁니다")
  @ResponseStatus(HttpStatus.ACCEPTED)
  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping("/{id}/rounds/next")
  public void proceedRound(@PathVariable Long id) {
    contestEventService.proceedRound(id);
  }

  @Operation(summary = "시도 넘기기", description = "선택된 종목의 시도를 다음으로 넘깁니다")
  @ResponseStatus(HttpStatus.ACCEPTED)
  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping("/{id}/runs")
  public void proceedRun(@PathVariable Long id) {
    contestEventService.proceedRun(id);
  }

  @Operation(summary = "종목 정보 반환", description = "선택된 종목의 정보를 반환합니다")
  @ResponseStatus(HttpStatus.ACCEPTED)
  @PreAuthorize("hasRole('ADMIN')")
  @GetMapping("/{id}")
  public void getContestEvent(@PathVariable Long id) {//TODO: DTO 만들기
    contestEventService.getContestEventById(id);
  }

  @Operation(summary = "라운드 정보 추가", description = "종목에 라운드를 추가합니다")
  @ResponseStatus(HttpStatus.ACCEPTED)
  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping("/{id}/rounds")
  public void addRound(@PathVariable Long id, @RequestBody AddRoundRequest request) {
    contestEventService.addRound(id, request.getRoundName(), request.getLimit());
  }

  @Operation(summary = "채점 정보 제출", description = "특정 시도에 대한 채점 정보를 제출합니다")
  @ResponseStatus(HttpStatus.ACCEPTED)
  @PreAuthorize("hasRole('JUDGE')")
  @PostMapping("/runs/{runId}")
  public void submitScore(@PathVariable Long runId, @RequestBody SubmitScoreRequest request) {
    judgeService.submitScore(runId, request.getJudgeId(), request.getScoreTotal(), request.getBreakdownJson());
  }
}

