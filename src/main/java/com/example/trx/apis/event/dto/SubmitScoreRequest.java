package com.example.trx.apis.event.dto;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SubmitScoreRequest {
  private Long judgeId;
  private BigDecimal scoreTotal;
  private String breakdownJson;
}
