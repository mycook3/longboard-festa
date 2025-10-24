package com.example.trx.domain.judge.exception;

import com.example.trx.domain.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public enum JudgeErrorCode implements ErrorCode {

    JUDGE_NOT_FOUND(HttpStatus.NOT_FOUND, "심사위원을 찾을 수 없습니다."),
    JUDGE_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 존재하는 심사위원 아이디입니다."),
    JUDGE_INVALID_CREDENTIALS(HttpStatus.UNAUTHORIZED, "잘못된 심사위원 인증 정보입니다.");

    private final HttpStatus status;
    private final String message;

    JudgeErrorCode(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
    }

    @Override
    public String getCode() {
        return name();
    }

    @Override
    public HttpStatus getStatus() {
        return status;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
