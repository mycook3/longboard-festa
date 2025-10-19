package com.example.trx.apis.event.dto.response;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class MatchResponse {
  private Long id;

  private String type;

  private Long participant1Id;
  private String participant1Name;
  private Long participant2Id;
  private String participant2Name;

  private List<RunResponse> runs;
}
