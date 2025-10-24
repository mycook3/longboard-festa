package com.example.trx.apis.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ErrorResponse {

    private final int status;
    private final String message;

    public static ErrorResponse of(int status, String message) {
        return ErrorResponse.builder()
            .status(status)
            .message(message)
            .build();
    }
}
