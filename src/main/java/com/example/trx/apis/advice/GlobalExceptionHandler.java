package com.example.trx.apis.advice;

import com.example.trx.apis.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ErrorResponse> handle(Throwable throwable) {
        HttpStatus status = resolveStatus(throwable);
        String message = resolveMessage(throwable, status);

        if (status.is5xxServerError()) {
            log.error("Unhandled exception", throwable);
        }

        return ResponseEntity.status(status)
            .body(ErrorResponse.of(status.value(), message));
    }

    private HttpStatus resolveStatus(Throwable throwable) {
        ResponseStatus responseStatus = AnnotatedElementUtils.findMergedAnnotation(
            throwable.getClass(), ResponseStatus.class);
        if (responseStatus != null) {
            HttpStatus status = responseStatus.code();
            if (status == HttpStatus.INTERNAL_SERVER_ERROR) {
                status = responseStatus.value();
            }
            if (status != HttpStatus.INTERNAL_SERVER_ERROR) {
                return status;
            }
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    private String resolveMessage(Throwable throwable, HttpStatus status) {
        String message = throwable.getMessage();
        if (message == null || message.isBlank()) {
            if (status == HttpStatus.INTERNAL_SERVER_ERROR) {
                return "내부 서버 오류가 발생했습니다.";
            }
            return status.getReasonPhrase();
        }
        return message;
    }
}
