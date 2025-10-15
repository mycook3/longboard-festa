package com.example.trx.domain.judge.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class JudgeNotFoundException extends RuntimeException {
    public JudgeNotFoundException(Long id) {
        super("심사위원을 찾을 수 없습니다. id=" + id);
    }

    public JudgeNotFoundException(String username) {
        super("심사위원을 찾을 수 없습니다. username=" + username);
    }
}
