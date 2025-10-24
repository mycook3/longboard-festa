package com.example.trx.domain.notice.exception;

import com.example.trx.domain.exception.ErrorCode;
import org.springframework.http.HttpStatus;

public enum NoticeErrorCode implements ErrorCode {

    NOTICE_NOT_FOUND(HttpStatus.NOT_FOUND, "공지(%d)를 찾을 수 없습니다."),
    INVALID_NOTICE_SCHEDULE(HttpStatus.BAD_REQUEST, "적용 시각은 현재 시각보다 과거일 수 없습니다.");

    private final HttpStatus status;
    private final String message;

    NoticeErrorCode(HttpStatus status, String message) {
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
