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
  private Long currentMatchId;
  private List<MatchResponse> matches;//토너먼트인 경우
  private List<RunResponse> runs;//점수 기반인 경우
}
