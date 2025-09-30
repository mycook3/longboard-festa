package com.example.trx.apis.admin.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class AdminResponse {
    private final Long id;
    private final String username;
}
