package com.example.trx.domain.event;

import com.example.trx.domain.judge.Judge;
import com.example.trx.domain.user.Participant;
import com.example.trx.domain.user.Participation;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;

//종목 정보
@Entity
@RequiredArgsConstructor
@MappedSuperclass
public abstract class ContestEvent {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Enumerated(EnumType.STRING)
  private Division division;

  @Enumerated(EnumType.STRING)
  private DisciplineCode disciplineCode;

  @Enumerated(EnumType.STRING)
  private Round round;

  @OneToMany(mappedBy = "contestEvent", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private List<Judge> judges;

  @OneToMany(mappedBy = "contestEvent", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private List<Participation> participations;

  public ContestEvent(Division division, DisciplineCode disciplineCode) {
    this.division = division;
    this.disciplineCode = disciplineCode;
    this.round = Round.PRELIMINARY;
    participations = new ArrayList<>();
  }

  //TODO
  public void addParticipant(Participant participant) {

  }

  public void proceedRoundAndDropParticipants() {
    Round nextRound = round.proceed();
    Integer limit = nextRound.getLimit();

    List<Participation> top = participations.stream()
        .sorted(
            //TODO
        )
        .limit(limit)
        .toList();

    participations.forEach(participant -> {
      if (!top.contains(participant)) {
        //TODO 탈락자 정보 기입(라운드)
      }
    });

    this.round = nextRound;
    participations.removeIf(participant -> !top.contains(participant));
  }
}
