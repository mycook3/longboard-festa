package com.example.trx.domain.event;

import com.example.trx.domain.user.Participant;
import java.util.ArrayList;
import java.util.List;

//종목별 라운드 진행 상황
public enum Round {
  PRELIMINARY(1000),//32강 선발
  ROUND_32(32),
  ROUND_16(16),
  ROUND_8(8),
  SEMI_FINAL(4),
  FINAL(2),
  WIN(1);

  private Integer limit;

  Round(int i) {
    this.limit = i;
  }

  public Integer getLimit() {
    return this.limit;
  }

  public Round proceed() throws IllegalStateException {
    return switch (this) {
      case PRELIMINARY -> ROUND_32;
      case ROUND_32 -> ROUND_16;
      case ROUND_16 -> ROUND_8;
      case ROUND_8 -> SEMI_FINAL;
      case SEMI_FINAL -> FINAL;
      case FINAL -> WIN;
      case WIN -> throw new IllegalStateException("종료된 종목입니다");
    };
  }
}
