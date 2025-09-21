package com.example.trx.service.notice;

import com.example.trx.apis.notice.dto.NoticeCreateRequest;
import com.example.trx.apis.notice.dto.NoticeResponse;
import com.example.trx.domain.notice.Notice;
import com.example.trx.domain.notice.NoticeImportance;
import com.example.trx.repository.notice.NoticeRepository;
import java.time.OffsetDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;

    @Transactional
    public NoticeResponse createNotice(NoticeCreateRequest request) {
        NoticeImportance importance = request.getImportance() != null
            ? request.getImportance()
            : NoticeImportance.NORMAL;
        OffsetDateTime applyAt = request.getApplyAt() != null
            ? request.getApplyAt()
            : OffsetDateTime.now();

        Notice notice = Notice.builder()
            .title(request.getTitle())
            .content(request.getContent())
            .importance(importance)
            .pinned(request.isPinned())
            .applyAt(applyAt)
            .build();

        Notice saved = noticeRepository.save(notice);

        return NoticeResponse.builder()
            .id(saved.getId())
            .title(saved.getTitle())
            .content(saved.getContent())
            .importance(saved.getImportance())
            .pinned(saved.isPinned())
            .applyAt(saved.getApplyAt())
            .createdAt(saved.getCreatedAt())
            .updatedAt(saved.getUpdatedAt())
            .build();
    }
}
