package com.example.trx.domain.event;

import com.example.trx.domain.judge.Judge;
import com.example.trx.domain.user.Participant;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.OneToMany;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
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
  private Level level;

  @Enumerated(EnumType.STRING)
  private Round round;

  @Enumerated(EnumType.STRING)
  private DisciplineCode disciplineCode;

  @OneToMany(mappedBy = "contestEvent", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private List<Judge> judges;

  @OneToMany(mappedBy = "contestEvent", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
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

    List<Participant> top = participants.stream()
        .sorted(
            //TODO total 점수 기반 정렬 로직
        )
        .limit(limit)
        .toList();

    participants.forEach(participant -> {
      if (!top.contains(participant)) {
        //TODO 탈락자 정보 기입(라운드)
      }
    });

    this.round = nextRound;
    participants.removeIf(participant -> !top.contains(participant));
  }
}
