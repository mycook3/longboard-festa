package com.example.trx.domain.event;

import com.example.trx.domain.judge.Judge;
import java.util.List;

//종목 정보
public abstract class ContestEvent {

  private Level level;
  private Round round;

  private List<Judge> judges;

  public ContestEvent(Level level) {
    this.level = level;
    this.round = Round.PRELIMINARY;
  }
}
