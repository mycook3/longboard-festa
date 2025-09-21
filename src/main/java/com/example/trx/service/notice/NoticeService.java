package com.example.trx.service.notice;

import com.example.trx.apis.notice.dto.NoticeCreateRequest;
import com.example.trx.apis.notice.dto.NoticeListResponse;
import com.example.trx.apis.notice.dto.NoticeResponse;
import com.example.trx.domain.notice.Notice;
import com.example.trx.domain.notice.NoticeImportance;
import com.example.trx.domain.notice.exception.InvalidNoticeScheduleException;
import com.example.trx.repository.notice.NoticeRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime applyAt = request.getApplyAt() != null
            ? request.getApplyAt()
            : now;

        if (applyAt.isBefore(now)) {
            throw new InvalidNoticeScheduleException("적용 시각은 현재 시각보다 과거일 수 없습니다.");
        }

        Notice notice = Notice.builder()
            .title(request.getTitle())
            .content(request.getContent())
            .importance(importance)
            .pinned(request.isPinned())
            .applyAt(applyAt)
            .build();

        Notice saved = noticeRepository.save(notice);

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public NoticeListResponse getNotices(int page, int size) {
        LocalDateTime now = LocalDateTime.now();

        Sort pinnedSort = Sort.by(Sort.Order.desc("createdAt"));
        List<NoticeResponse> pinnedNotices = noticeRepository
            .findByPinnedIsTrueAndApplyAtLessThanEqual(now, pinnedSort)
            .stream()
            .map(this::toResponse)
            .collect(Collectors.toList());

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Order.desc("createdAt")));
        Page<Notice> pageResult = noticeRepository
            .findByPinnedIsFalseAndApplyAtLessThanEqual(now, pageable);

        List<NoticeResponse> generalContents = pageResult.getContent().stream()
            .map(this::toResponse)
            .collect(Collectors.toList());

        NoticeListResponse.PagedNotices paged = NoticeListResponse.PagedNotices.builder()
            .contents(generalContents)
            .page(pageResult.getNumber())
            .size(pageResult.getSize())
            .totalElements(pageResult.getTotalElements())
            .totalPages(pageResult.getTotalPages())
            .last(pageResult.isLast())
            .build();

        return NoticeListResponse.builder()
            .pinned(pinnedNotices)
            .publics(paged)
            .build();
    }

    private NoticeResponse toResponse(Notice notice) {
        return NoticeResponse.builder()
            .id(notice.getId())
            .title(notice.getTitle())
            .content(notice.getContent())
            .importance(notice.getImportance())
            .pinned(notice.isPinned())
            .applyAt(notice.getApplyAt())
            .createdAt(notice.getCreatedAt())
            .updatedAt(notice.getUpdatedAt())
            .build();
    }
}
