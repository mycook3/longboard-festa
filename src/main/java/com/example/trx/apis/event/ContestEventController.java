package com.example.trx.apis.event;

import com.example.trx.apis.dto.ApiResult;
import com.example.trx.apis.event.dto.response.ContestEventResponse;
import com.example.trx.apis.event.dto.request.EditScoreRequest;
import com.example.trx.apis.event.dto.request.SubmitScoreRequest;
import com.example.trx.service.event.ContestEventApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/contest")
@RequiredArgsConstructor
@Tag(name = "ContestEvents", description = "대회 진행 종목 관리 API")
public class ContestEventController {

  private final ContestEventApplicationService contestEventService;

  @Operation(summary = "종목 시작", description = "선택된 종목을 시작합니다")
  @ResponseStatus(HttpStatus.ACCEPTED)
  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping("/{id}")
  public ApiResult<Void> startContestEvent(@PathVariable Long id) {
    contestEventService.initContest(id);
    contestEventService.startContestEvent(id);
    return ApiResult.succeed(null);
  }

  @Operation(summary = "라운드 넘기기", description = "선택된 종목의 라운드를 다음으로 넘깁니다")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping("/{id}/rounds/next")
  public ApiResult<Void> proceedRound(@PathVariable Long id) {
    contestEventService.proceedRound(id);
    return ApiResult.succeed(null);
  }

  @Operation(summary = "시도 넘기기", description = "선택된 종목의 시도를 다음으로 넘깁니다")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping("/{id}/runs")
  public ApiResult<Void> proceedRun(@PathVariable Long id) {
    contestEventService.proceedRun(id);
    return ApiResult.succeed(null);
  }

  @Operation(summary = "ID 기반 종목 정보 반환", description = "선택된 종목의 정보를 반환합니다")
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/{id}")
  public ApiResult<ContestEventResponse> getContestEvent(@PathVariable Long id) {
    return ApiResult.succeed(contestEventService.getContestEventById(id));
  }

  @Operation(summary = "종목명 & Division 기반 종목 정보 반환", description = "선택된 종목의 정보를 반환합니다")
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/")
  public ApiResult<ContestEventResponse> getContestEventByTypeAndDivision(
      @RequestParam(required = true) String eventName,
      @RequestParam(required = true) String division
  ) {
    return ApiResult.succeed(contestEventService.getContestEventByEventNameAndDivision(eventName, division));
  }

  @Operation(summary = "채점 정보 제출", description = "특정 시도에 대한 채점 정보를 제출합니다")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize("hasRole('JUDGE')")
  @PostMapping("/runs/{runId}")
  public ApiResult<Void> submitScore(@PathVariable Long runId, @RequestBody SubmitScoreRequest request) {
    contestEventService.submitScore(runId, request);
    return ApiResult.succeed(null);
  }

  @Operation(summary = "채점 정보 수정", description = "이미 제출된 채점 정보를 수정합니다")
  @PreAuthorize("hasRole('ADMIN')")
  @ResponseStatus(HttpStatus.OK)
  @PutMapping("/scores/{scoreId}")
  public ApiResult<Void> editScore(@PathVariable Long scoreId, @RequestBody EditScoreRequest request) {
    contestEventService.editScore(scoreId, request);
    return ApiResult.succeed(null);
  }
}

