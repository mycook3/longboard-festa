package com.example.trx.domain.event.exception;

import com.example.trx.domain.event.DisciplineCode;
import com.example.trx.domain.event.Division;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class ContestEventAlreadyExistsException extends RuntimeException {
      public ContestEventAlreadyExistsException(Division division, DisciplineCode disciplinecode) {
        super("이미 등록된 종목입니다. " + division.name() + "-" + disciplinecode.name());
    }
}
