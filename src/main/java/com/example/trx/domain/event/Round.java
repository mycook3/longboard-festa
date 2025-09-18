package com.example.trx.domain.event;

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
}
