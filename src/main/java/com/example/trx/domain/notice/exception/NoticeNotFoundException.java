package com.example.trx.domain.notice.exception;

import com.example.trx.domain.exception.BusinessException;

public class NoticeNotFoundException extends BusinessException {

    public NoticeNotFoundException(Long id) {
        super(NoticeErrorCode.NOTICE_NOT_FOUND, id);
    }
}
