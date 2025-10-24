package com.example.trx.domain.exception;

public class BusinessException extends RuntimeException {

    private final ErrorCode errorCode;

    public BusinessException(ErrorCode errorCode, Object... messageArgs) {
        super(formatMessage(errorCode, messageArgs));
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    private static String formatMessage(ErrorCode errorCode, Object... messageArgs) {
        String message = errorCode.getMessage();
        if (messageArgs != null && messageArgs.length > 0) {
            return String.format(message, messageArgs);
        }
        return message;
    }
}
