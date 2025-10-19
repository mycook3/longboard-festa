package com.example.trx.apis.admin;

import com.example.trx.apis.dto.ApiResult;
import com.example.trx.apis.notice.dto.NoticeSummaryResponse;
import com.example.trx.service.notice.NoticeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/notices")
@RequiredArgsConstructor
@Tag(name = "Admin Notices", description = "관리자 공지 관리 API")
public class AdminNoticeController {

    private final NoticeService noticeService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "전체 공지 조회", description = "삭제되지 않은 모든 공지를 등록일 내림차순으로 조회합니다.")
    public ApiResult<List<NoticeSummaryResponse>> getAllNotices() {
        return ApiResult.succeed(noticeService.getAllNoticesForAdmin());
    }
}
