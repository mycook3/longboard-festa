package com.example.trx.domain.event.exception;

import com.example.trx.domain.event.DisciplineCode;
import com.example.trx.domain.event.Division;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ContestEventNotFound extends RuntimeException {
    public ContestEventNotFound(Division division, DisciplineCode disciplinecode) {
        super("해당 종목을 찾을 수 없습니다." + division.name() + "-" + disciplinecode.name());
    }
}
