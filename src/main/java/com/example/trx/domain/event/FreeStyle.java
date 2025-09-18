package com.example.trx.domain.event;

import com.example.trx.domain.user.DisciplineCode;

public class FreeStyle extends ContestEvent{

  public FreeStyle(Level level) {
    super(level, DisciplineCode.FREESTYLE);
  }

}
