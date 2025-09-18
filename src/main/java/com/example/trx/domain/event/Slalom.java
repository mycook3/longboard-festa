package com.example.trx.domain.event;

public class Slalom extends ContestEvent{

  public Slalom(Level level) {
    super(level, DisciplineCode.SLALOM);
  }

}
