package com.example.trx.apis.event.dto.request;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SubmitScoreRequest {
  private Long judgeId;
  private BigDecimal scoreTotal;
  private String breakdownJson;
}
