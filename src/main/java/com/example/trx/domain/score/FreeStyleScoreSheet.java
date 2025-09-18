package com.example.trx.domain.score;

import com.example.trx.domain.event.ContestEvent;
import java.math.BigDecimal;

public class FreeStyleScoreSheet extends ScoreSheet {

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
