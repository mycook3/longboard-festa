package com.example.trx.domain.judge.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNAUTHORIZED)
public class JudgeInvalidCredentialsException extends RuntimeException {
    public JudgeInvalidCredentialsException() {
        super("잘못된 심사위원 인증 정보입니다.");
    }
}
