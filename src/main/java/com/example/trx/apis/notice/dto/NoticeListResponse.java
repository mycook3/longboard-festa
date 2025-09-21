package com.example.trx.apis.notice.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class NoticeListResponse {
    private final List<NoticeResponse> pinned;
    private final PagedNotices publics;

    @Getter
    @AllArgsConstructor
    @Builder
    public static class PagedNotices {
        private final List<NoticeResponse> contents;
        private final int page;
        private final int size;
        private final long totalElements;
        private final int totalPages;
        private final boolean last;
    }
}
