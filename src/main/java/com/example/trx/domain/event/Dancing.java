package com.example.trx.domain.event;

import com.example.trx.domain.user.DisciplineCode;

public class Dancing extends ContestEvent{

  public Dancing(Level level) {
    super(level, DisciplineCode.DANCING);
  }

}
