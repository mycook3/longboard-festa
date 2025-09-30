package com.example.trx.apis.judge.dto;

import com.example.trx.domain.event.DisciplineCode;
import com.example.trx.domain.judge.JudgeStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JudgeUpdateRequest {

    @NotBlank
    private String name;

    @NotNull
    private DisciplineCode disciplineCode;

    @NotNull
    private JudgeStatus status;
}
