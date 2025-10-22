package com.example.trx.apis.judge.dto;

import com.example.trx.domain.event.DisciplineCode;
import com.example.trx.domain.judge.JudgeStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class JudgeResponse {
    private final Long id;
    private final String name;
    private final String username;
    private final Integer judgeNumber;
    private final JudgeStatus status;
}
