package com.example.trx.support.valid;

import lombok.Getter;

import java.util.Collections;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Getter
public enum ValidationStatus {
    VALID("VALID"),
    INVALID("INVALID");

    private static final Map<String, ValidationStatus> codes =
            Collections.unmodifiableMap(Stream.of(values())
                    .collect(Collectors.toMap(ValidationStatus::getCode, Function.identity())));
    private String code;

    ValidationStatus(String code) {
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
