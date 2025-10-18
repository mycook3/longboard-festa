package com.example.trx.apis.event.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class EditRoundRequest {
  private String roundName;
  private Integer limit;
}
