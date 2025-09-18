package com.example.trx.domain.score;

import com.example.trx.domain.judge.Judge;
import com.example.trx.domain.user.Participant;
import java.math.BigDecimal;

public abstract class ScoreSheet {

  private Participant participant;
  private Judge judge;

  public abstract BigDecimal getTotal();
}
