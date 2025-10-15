package com.example.trx.apis.event.dto;

import com.example.trx.domain.event.ContestEvent;
import java.util.List;

public class ContestEventResponse {
  private Long id;
  private String name;
  private String division;
  private String eventName;
  private String status;
  private List<RunResponse> runs;
}
