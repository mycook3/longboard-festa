package com.example.trx.apis.event.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class RoundResponse {
  private Long id;
  private String name;
  private Integer participantLimit;
  private String status;
  private Long currentRunId;
  private List<RunResponse> runs;
}
