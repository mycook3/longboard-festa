package com.example.trx.domain.event;

public class Dancing extends ContestEvent {

  public Dancing(Division division) {
    super(division, DisciplineCode.DANCING);
  }

}
