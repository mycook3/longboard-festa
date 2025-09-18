package com.example.trx.domain.event;

public class FreeStyle extends ContestEvent{

  public FreeStyle(Division division) {
    super(division, DisciplineCode.FREESTYLE);
  }

}
