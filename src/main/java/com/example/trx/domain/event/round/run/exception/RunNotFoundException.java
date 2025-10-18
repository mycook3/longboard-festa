package com.example.trx.domain.event.round.run.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class RunNotFoundException extends RuntimeException {
    public RunNotFoundException(Long id) {
        super("해당 시도를 찾을 수 없습니다. id=" + id);
    }
}
