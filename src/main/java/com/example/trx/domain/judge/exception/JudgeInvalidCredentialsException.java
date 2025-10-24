package com.example.trx.domain.judge.exception;

import com.example.trx.domain.exception.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class JudgeInvalidCredentialsException extends BusinessException {

    public JudgeInvalidCredentialsException() {
        super(JudgeErrorCode.JUDGE_INVALID_CREDENTIALS);
    }
}
