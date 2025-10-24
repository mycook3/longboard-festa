package com.example.trx.domain.judge.exception;

import com.example.trx.domain.exception.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class JudgeAlreadyExistsException extends BusinessException {

    public JudgeAlreadyExistsException(String username) {
        super(JudgeErrorCode.JUDGE_ALREADY_EXISTS, username);
    }
}
