package com.example.trx.apis.event.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AddRoundRequest {
  private Long contestId;
  private String roundName;
  private Integer limit;
  private Integer runPerParticipant;
}
