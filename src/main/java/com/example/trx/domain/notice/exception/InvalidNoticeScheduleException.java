package com.example.trx.domain.notice.exception;

public class InvalidNoticeScheduleException extends RuntimeException {
    public InvalidNoticeScheduleException(String message) {
        super(message);
    }
}
