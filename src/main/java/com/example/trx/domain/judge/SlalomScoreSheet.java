package com.example.trx.domain.judge;

import com.example.trx.domain.event.ContestEvent;
import com.example.trx.domain.user.Participant;

public class SlalomScoreSheet {

  private Participant participant;
  private Judge judge;
  private ContestEvent contestEvent;

  private Integer record;
  private Integer touch;
  private Integer score;//정성점수

  public Double getTotalScore() {
    return 100 - (touch * 0.5) + score;
  }
}
