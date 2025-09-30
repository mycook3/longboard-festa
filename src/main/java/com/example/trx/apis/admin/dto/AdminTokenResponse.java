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

    public enum TokenType {
        BEARER
    }
}
