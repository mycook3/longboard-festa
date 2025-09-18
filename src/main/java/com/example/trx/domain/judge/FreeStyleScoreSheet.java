package com.example.trx.domain.judge;

import com.example.trx.domain.event.ContestEvent;
import com.example.trx.domain.user.Participant;
import java.math.BigDecimal;

public class FreeStyleScoreSheet extends ScoreSheet {

  private ContestEvent contestEvent;

  private Integer difficulty;
  private Integer representation;
  private Integer stability;
  private Integer reaction;
  private Integer etc;

  @Override
  public BigDecimal getTotal() {
    return new BigDecimal(difficulty + representation + stability + reaction + etc);
  }
}
