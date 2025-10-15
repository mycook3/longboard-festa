package com.example.trx.apis.event.dto.response;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class ScoreResponse {
  private Long id;
  private Long judgeId;
  private String judgeName;
  private BigDecimal score;
}
