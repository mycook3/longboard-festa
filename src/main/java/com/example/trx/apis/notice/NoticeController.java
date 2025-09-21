package com.example.trx.apis.notice;

import com.example.trx.apis.dto.ApiResult;
import com.example.trx.apis.notice.dto.NoticeCreateRequest;
import com.example.trx.apis.notice.dto.NoticeListResponse;
import com.example.trx.apis.notice.dto.NoticeResponse;
import com.example.trx.service.notice.NoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notices")
@RequiredArgsConstructor
@Tag(name = "Notice", description = "공지 관리 API")
public class NoticeController {

    private final NoticeService noticeService;

    @Operation(summary = "공지 등록", description = "관리자가 신규 공지를 등록합니다.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResult<NoticeResponse> createNotice(@Valid @RequestBody NoticeCreateRequest request) {
        return ApiResult.succeed(noticeService.createNotice(request));
    }

    @Operation(summary = "공지 조회", description = "상단 고정 공지와 일반 공지를 분리해 조회합니다.")
    @GetMapping
    public ApiResult<NoticeListResponse> getNotices(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size
    ) {
        return ApiResult.succeed(noticeService.getNotices(page, size));
    }
}
