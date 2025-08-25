package com.example.trx.apis.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Getter
public class ApiResult<T> {

    @JsonProperty("data")
    private final T data;
    @JsonProperty("message")
    private final String message;
    private final ResultStatus status;

    public ApiResult(T data, String message, ResultStatus status) {
        this.data = data;
        this.message = message;
        this.status = status;
    }

    public static <T> ApiResult<T> succeed(T data) {
        return succeed(data, null);
    }

    public static <T> ApiResult<T> succeed(T data, String message) {
        return new ApiResult<>(data, message, ResultStatus.SUCCESS);
    }

    public static <T> ApiResult<T> failed(Throwable throwable) {
        return failed(null, throwable.getMessage());
    }

    public static <T> ApiResult<T> failed(T data, Throwable throwable) {
        return failed(data, throwable.getMessage());
    }

    public static <T> ApiResult<T> failed(String message) {
        return failed(null, message);
    }

    public static <T> ApiResult<T> failed(T data, String message) {
        return new ApiResult<>(data, message, ResultStatus.ERROR);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
                .append("data", data)
                .append("message", message)
                .append("status", status)
                .toString();
    }
}
