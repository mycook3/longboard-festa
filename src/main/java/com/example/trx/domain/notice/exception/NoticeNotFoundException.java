package com.example.trx.domain.notice.exception;

public class NoticeNotFoundException extends RuntimeException {
    public NoticeNotFoundException(Long id) {
        super("공지(" + id + ")를 찾을 수 없습니다.");
    }
}
