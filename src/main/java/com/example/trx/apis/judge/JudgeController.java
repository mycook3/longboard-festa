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

    @Operation(summary = "심사위원 정보 수정", description = "관리자가 심사위원 정보를 수정합니다.")
    @PutMapping("/{judgeId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ApiResult<JudgeResponse> updateJudge(
        @PathVariable Long judgeId,
        @Valid @RequestBody JudgeUpdateRequest request
    ) {
        return ApiResult.succeed(judgeService.updateJudge(judgeId, request));
    }
}
