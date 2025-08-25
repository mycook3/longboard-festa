package com.example.trx.apis.dto;

import lombok.Getter;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;


@Getter
public enum ResultStatus {

    SUCCESS("SUCCESS"),
    LOG("LOG"),
    ERROR("ERROR");

        private static final Map<String, ResultStatus> codes =
                Collections.unmodifiableMap(Stream.of(values())
                        .collect(Collectors.toMap(ResultStatus::getCode, Function.identity())));
        private String code;

        ResultStatus(String code) {
            this.code = code;
        }

    @Override
    public String toString() {
        return code;
    }

    public static boolean contains(String code) {
            return codes.get(code) != null;
        }


}
