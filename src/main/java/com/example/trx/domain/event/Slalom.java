package com.example.trx.domain.event;

import com.example.trx.domain.user.DisciplineCode;

public class Slalom extends ContestEvent{

  public Slalom(Level level) {
    super(level, DisciplineCode.SLALOM);
  }

}
