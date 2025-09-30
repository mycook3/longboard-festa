package com.example.trx.domain.judge.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class JudgeAlreadyExistsException extends RuntimeException {
    public JudgeAlreadyExistsException(String username) {
        super("이미 존재하는 심사위원 아이디입니다. username=" + username);
    }
}
