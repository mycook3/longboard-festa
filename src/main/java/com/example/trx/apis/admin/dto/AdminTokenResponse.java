package com.example.trx.apis.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class AdminTokenResponse {
    private final TokenType tokenType;
    private final String token;
    private final Long judgeId;

    public enum TokenType {
        BEARER
    }
}
