package com.example.trx.domain.score;

import com.example.trx.domain.event.ContestEvent;
import com.example.trx.domain.judge.Judge;
import com.example.trx.domain.user.Participant;
import java.math.BigDecimal;

public class SlalomScoreSheet extends ScoreSheet {

  private Participant participant;
  private Judge judge;
  private ContestEvent contestEvent;

  private Integer record;
  private Integer touch;
  private Integer score;//정성점수

  @Override
  public BigDecimal getTotal() {
    return new BigDecimal(100 - (touch * 0.5) + score);
  }
}
