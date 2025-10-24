package com.example.trx.domain.notice.exception;

import com.example.trx.domain.exception.BusinessException;

public class InvalidNoticeScheduleException extends BusinessException {

    public InvalidNoticeScheduleException() {
        super(NoticeErrorCode.INVALID_NOTICE_SCHEDULE);
    }
}
