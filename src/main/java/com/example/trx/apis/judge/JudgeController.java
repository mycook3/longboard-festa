package com.example.trx.apis.judge;

import com.example.trx.apis.dto.ApiResult;
import com.example.trx.apis.judge.dto.JudgeCreateRequest;
import com.example.trx.apis.judge.dto.JudgeResponse;
import com.example.trx.apis.judge.dto.JudgeUpdateRequest;
import com.example.trx.service.judge.JudgeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/judges")
@RequiredArgsConstructor
@Tag(name = "Judges", description = "심사위원 관리 API")
public class JudgeController {

    private final JudgeService judgeService;

    @Operation(summary = "심사위원 등록", description = "관리자가 심사위원 계정을 생성합니다.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<JudgeResponse> createJudge(@Valid @RequestBody JudgeCreateRequest request) {
        return ApiResult.succeed(judgeService.createJudge(request));
    }

    @Operation(summary = "심사위원 목록 조회", description = "심사위원의 기본 정보를 조회합니다.")
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<List<JudgeResponse>> getJudges() {
        return ApiResult.succeed(judgeService.getJudges());
    }

    @Operation(summary = "심사위원 정보 수정", description = "관리자가 심사위원 정보를 수정합니다.")
    @PutMapping("/{judgeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<JudgeResponse> updateJudge(
        @PathVariable Long judgeId,
        @Valid @RequestBody JudgeUpdateRequest request
    ) {
        return ApiResult.succeed(judgeService.updateJudge(judgeId, request));
    }

    @Operation(summary = "심사위원 비활성화", description = "심사위원을 사용 불가 상태로 전환합니다.")
    @DeleteMapping("/{judgeId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<Void> deactivateJudge(@PathVariable Long judgeId) {
        judgeService.deactivateJudge(judgeId);
        return ApiResult.succeed(null);
    }
}
