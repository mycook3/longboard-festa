package com.example.trx.apis.event.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AddRoundRequest {
  private String roundName;
  private Integer limit;
}
