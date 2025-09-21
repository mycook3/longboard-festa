package com.example.trx.apis.notice.dto;

import com.example.trx.domain.notice.NoticeImportance;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoticeUpdateRequest {

    @NotBlank
    private String title;

    @NotBlank
    private String content;

    private boolean pinned;

    private NoticeImportance importance;

    private LocalDateTime applyAt;
}
