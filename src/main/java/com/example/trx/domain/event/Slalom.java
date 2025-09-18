package com.example.trx.domain.event;

public class Slalom extends ContestEvent{

  public Slalom(Division division) {
    super(division, DisciplineCode.SLALOM);
  }

}
