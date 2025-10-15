package com.example.trx.apis.event.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateContestEventRequest {

  @NotBlank
  private String disciplineCode;

  @NotBlank
  private String division;
}
