package com.example.trx.domain.event;

import com.example.trx.domain.judge.Judge;
import com.example.trx.domain.run.Run;
import com.example.trx.domain.score.ScoreTotal;
import com.example.trx.domain.user.Participant;
import com.example.trx.domain.user.Participation;
import com.example.trx.domain.user.ParticipationStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.NoArgsConstructor;

//종목 정보
@Entity
@NoArgsConstructor
public class ContestEvent {

  public ContestEvent(Division division, DisciplineCode disciplineCode) {
    this.division = division;
    this.disciplineCode = disciplineCode;
    this.round = Round.PRELIMINARY;
  }

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  // 수준
  @Enumerated(EnumType.STRING)
  private Division division;

  // 종목 정보
  @Enumerated(EnumType.STRING)
  private DisciplineCode disciplineCode;

  // 현재 진행 중인 라운드
  @Enumerated(EnumType.STRING)
  private Round round;

  // 심사위원
  @OneToMany(mappedBy = "contestEvent", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private List<Judge> judges = new ArrayList<>();

  // 해당 종목에서 심사된 모든 시도
  @OneToMany(mappedBy = "contestEvent", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private List<Run> runs = new ArrayList<>();

  public void addRun(Participant participant){
    Run run = new Run(participant, this.round, this);
    runs.add(run);
  }

  public void proceedRoundAndDropParticipants() {
    Round nextRound = round.proceed();
    Integer limit = nextRound.getLimit();

    //다음 라운드로 넘어갈 사람들만 추리기
    //TODO 동점자 처리 의논 필요
    List<Run> top = runs.stream().filter(run -> round.equals(run.getRound()))
        .sorted(
           Comparator.comparing((Run run) -> {
                List<BigDecimal> scores = run.getScores()
                                             .stream()
                                             .map(ScoreTotal::getTotal)
                                             .sorted()
                                             .toList();

                return scores.subList(1, scores.size() - 1)
                             .stream()
                             .reduce(BigDecimal.ZERO, BigDecimal::add);
            }).reversed()
        )
        .limit(limit)
        .toList();

    this.round = nextRound;//라운드 진행
    top.forEach(run -> this.addRun(run.getParticipant()));
  }
}
