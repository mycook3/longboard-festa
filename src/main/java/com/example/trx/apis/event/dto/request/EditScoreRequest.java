package com.example.trx.apis.event.dto.request;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EditScoreRequest {
  private BigDecimal scoreTotal;
  private String breakdownJson;
  private String editedBy;
  private String editReason;
}
