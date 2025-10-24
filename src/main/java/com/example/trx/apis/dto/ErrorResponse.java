package com.example.trx.apis.dto;

import com.example.trx.domain.exception.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ErrorResponse {

    private final String code;
    private final int status;
    private final String message;

    public static ErrorResponse of(int status, String message) {
        return ErrorResponse.builder()
            .status(status)
            .message(message)
            .build();
    }

    public static ErrorResponse of(ErrorCode errorCode, String message) {
        return ErrorResponse.builder()
            .code(errorCode.getCode())
            .status(errorCode.getStatus().value())
            .message(message)
            .build();
    }
}
