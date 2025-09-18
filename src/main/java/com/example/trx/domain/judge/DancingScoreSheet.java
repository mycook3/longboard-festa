package com.example.trx.domain.judge;

import java.math.BigDecimal;

public class DancingScoreSheet extends ScoreSheet{

  private Integer flow;
  private Integer speedControl;
  private Integer difficulty;
  private Integer style;
  private Integer creativity;


  @Override
  public BigDecimal getTotal() {
    return new BigDecimal(difficulty + flow + speedControl + style + creativity);
  }
}
