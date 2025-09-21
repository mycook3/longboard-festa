package com.example.trx.apis.notice.dto;

import com.example.trx.domain.notice.NoticeImportance;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class NoticeResponse {
    private final Long id;
    private final String title;
    private final String content;
    private final NoticeImportance importance;
    private final boolean pinned;
    private final OffsetDateTime applyAt;
    private final OffsetDateTime createdAt;
    private final OffsetDateTime updatedAt;
}
