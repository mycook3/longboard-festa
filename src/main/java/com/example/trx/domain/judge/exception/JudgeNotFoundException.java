package com.example.trx.domain.judge.exception;

import com.example.trx.domain.exception.BusinessException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class JudgeNotFoundException extends BusinessException {

    public JudgeNotFoundException(Long id) {
        super(JudgeErrorCode.JUDGE_NOT_FOUND, "id", id);
    }

    public JudgeNotFoundException(String username) {
        super(JudgeErrorCode.JUDGE_NOT_FOUND, "username", username);
    }
}
