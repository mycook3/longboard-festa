package com.example.trx.apis.event.dto.response;

import java.math.BigDecimal;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RunResponse {
  private Long id;
  private Integer attemptNumber;
  private Long participantId;
  private String participantName;
  private String status;
  private BigDecimal record;
  private Integer touch;
  private List<ScoreResponse> scores;
}
