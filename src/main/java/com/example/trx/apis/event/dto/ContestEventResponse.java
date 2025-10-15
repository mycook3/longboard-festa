package com.example.trx.apis.event.dto;

import com.example.trx.domain.event.ContestEvent;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ContestEventResponse {
  private Long id;
  private String eventName;
  private String division;
  private String status;
  private String currentRound;
  private List<RunResponse> runs;
}
