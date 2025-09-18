package com.example.trx.domain.event;

import com.example.trx.domain.judge.Judge;
import com.example.trx.domain.user.DisciplineCode;
import com.example.trx.domain.user.Participant;
import java.util.ArrayList;
import java.util.List;
import org.springdoc.core.models.ParameterId;

//종목 정보
public abstract class ContestEvent {

  private Level level;
  private Round round;
  private DisciplineCode disciplineCode;

  private List<Judge> judges;
  private List<Participant> participants;

  public ContestEvent(Level level, DisciplineCode disciplineCode) {
    this.level = level;
    this.disciplineCode = disciplineCode;
    this.round = Round.PRELIMINARY;
    participants = new ArrayList<>();
  }

  public void addParticipant(Participant participant) {
    participants.add(participant);
  }

  public void proceedRoundAndDropParticipants() {
    Round nextRound = round.proceed();
    Integer limit = nextRound.getLimit();

    //Score를 가지고 sort후 limit만큼 필터링. 내역을 저장하고 삭제
  }
}
