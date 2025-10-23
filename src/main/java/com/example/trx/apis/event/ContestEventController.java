package com.example.trx.apis.event;

import com.example.trx.apis.dto.ApiResult;
import com.example.trx.apis.event.dto.request.AddRoundRequest;
import com.example.trx.apis.event.dto.request.EditRoundRequest;
import com.example.trx.apis.event.dto.request.ManualWinnerRequest;
import com.example.trx.apis.event.dto.response.ContestEventResponse;
import com.example.trx.apis.event.dto.request.EditScoreRequest;
import com.example.trx.apis.event.dto.request.SubmitScoreRequest;
import com.example.trx.service.event.ContestEventApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
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

  @Operation(summary = "대회 시작", description = "모든 종목을 초기화하고 참가자를 배정합니다")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping("/init")
  public ApiResult<Void> initContest() {
    contestEventService.initAll();
    return ApiResult.succeed(null);
  }

  @Operation(summary = "초기화 여부 반환", description = "모든 종목이 제대로 초기화되었는지 확인합니다")
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/init/status")
  public ApiResult<Boolean> isAllContestInitialized() {
    Boolean isAllContestInitialized = contestEventService.isContestInitialized();
    return ApiResult.succeed(isAllContestInitialized);
  }

  @Deprecated
  @Operation(summary = "종목 시작", description = "선택된 종목을 시작합니다")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping("/{id}")
  public ApiResult<Void> startContestEvent(@PathVariable Long id) {
    contestEventService.startContestEvent(id);
    return ApiResult.succeed(null);
  }

  @Deprecated
  @Operation(summary = "종목 종료", description = "선택된 종목을 종료합니다")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping("/{id}/end")
  public ApiResult<Void> endContestEvent(@PathVariable Long id) {
    contestEventService.endContestEvent(id);
    return ApiResult.succeed(null);
  }

  @Operation(summary = "라운드 넘기기", description = "현재 라운드를 종료하고 다음 라운드를 찾아 설정합니다")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping("/{id}/rounds/next")
  public ApiResult<Void> proceedRound(@PathVariable Long id) {
    contestEventService.proceedRound(id);
    return ApiResult.succeed(null);
  }

  @Operation(summary = "현재 라운드 시작", description = "현재 설정된 라운드를 시작합니다")
  @ResponseStatus(HttpStatus.OK)
  @PreAuthorize("hasRole('ADMIN')")
  @PostMapping("/{id}/rounds/start")
  public ApiResult<Void> startCurrentRound(@PathVariable Long id) {
    contestEventService.startCurrentRound(id);
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

  @Operation(summary = "종목명, division, 라운드명 기반 종목 정보 반환", description = "선택된 종목의 정보를 반환합니다")
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("")
  public ApiResult<ContestEventResponse> getContestEventByTypeAndDivision(
      @RequestParam(required = true) String event,
      @RequestParam(required = true) String division,
      @RequestParam(required = false) List<String> round
  ) {
    return ApiResult.succeed(contestEventService.getContestEventByEventNameAndDivision(event, division, round));
  }

  @Operation(summary = "현재 진행 중인 라운드 정보를 반환", description = "현재 진행 중인 모든 종목의 현재 라운드 정보를 반환")
  @ResponseStatus(HttpStatus.OK)
  @GetMapping("/current")
  public ApiResult<List<ContestEventResponse>> getContestsInProgressFilteringCurrentRound() {
    return ApiResult.succeed(contestEventService.getContestEventsRoundInProgress());
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

  @Deprecated
  @Operation(summary = "라운드 생성", description = "새 라운드를 생성합니다")
  @PreAuthorize("hasRole('ADMIN')")
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/{id}/rounds")
  public ApiResult<Void> addRound(@PathVariable Long id, @RequestBody AddRoundRequest request) {
    contestEventService.addRound(id, request);
    return ApiResult.succeed(null);
  }

  @Deprecated
  @Operation(summary = "라운드 정보 변경", description = "해당 라운드의 정보를 변경합니다")
  @PreAuthorize("hasRole('ADMIN')")
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/rounds/{id}")
  public ApiResult<Void> addRound(@PathVariable Long id, @RequestBody EditRoundRequest request) {
    contestEventService.editRound(id, request);
    return ApiResult.succeed(null);
  }

  @Deprecated
  @Operation(summary = "라운드 삭제", description = "라운드 정보를 삭제합니다.")
  @PreAuthorize("hasRole('ADMIN')")
  @ResponseStatus(HttpStatus.OK)
  @DeleteMapping("/rounds/{id}")
  public ApiResult<Void> deleteRound(@PathVariable Long id) {
    contestEventService.deleteRound(id);
    return ApiResult.succeed(null);
  }

  @Operation(summary = "토너먼트 매치 부전승 처리", description = "주어진 토너먼트 매치의 모든 참가자를 승리 처리합니다.")
  @PreAuthorize("hasRole('ADMIN')")
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/match/{id}/bye")
  public ApiResult<Void> makeMatchBye(@PathVariable Long id) {
    contestEventService.makeMatchBye(id);
    return ApiResult.succeed(null);
  }

  @Operation(summary = "토너먼트 매치 수동 승리 처리", description = "매치 참가자 중 한 명을 승자로 처리합니다(동점자 처리 시)")
  @PreAuthorize("hasRole('ADMIN')")
  @ResponseStatus(HttpStatus.OK)
  @PostMapping("/match/{id}/winner")
  public ApiResult<Void> makeManualWinner(
      @PathVariable Long id,
      @RequestBody ManualWinnerRequest request
  ) {
    contestEventService.makeManualParticipant(id, request.getParticipantId());
    return ApiResult.succeed(null);
  }

}

