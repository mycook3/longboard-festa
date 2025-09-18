package com.example.trx.domain.judge;

import com.example.trx.domain.event.ContestEvent;
import com.example.trx.domain.user.Participant;
import java.math.BigDecimal;

public abstract class ScoreSheet {

  private Participant participant;
  private Judge judge;

  public abstract BigDecimal getTotal();
}
