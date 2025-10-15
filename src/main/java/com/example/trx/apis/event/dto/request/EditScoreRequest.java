package com.example.trx.apis.event.dto.request;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EditScoreRequest {
  private Long judgeId;
  private BigDecimal scoreTotal;
  private String breakdownJson;
  private String editedBy;
  private String editReason;
}
