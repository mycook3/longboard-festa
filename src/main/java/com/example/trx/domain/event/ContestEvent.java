package com.example.trx.domain.event;

import com.example.trx.domain.run.Run;
import com.example.trx.domain.score.ScoreTotal;
import com.example.trx.domain.user.Participant;
import com.example.trx.domain.user.UserStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

//종목 정보
@Entity
@Getter
@Builder
@AllArgsConstructor
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

  // 현재 진행 중인 것
  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = "current_run_id")
  private Run currentRun;

  // 해당 종목에서 심사된 모든 시도
  @OneToMany(mappedBy = "contestEvent", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @Builder.Default
  private List<Run> runs = new ArrayList<>();

   //TODO
  public void init() {
    Optional<Run> nextRun = runs.stream()
        .filter(run -> run.getRound().equals(this.round))
        .filter(run -> !run.getUserStatus().equals(UserStatus.DONE)) // 아직 끝나지 않은 Run
        .findFirst();

    this.currentRun = nextRun.orElse(null);
  }

  public void addRun(Participant participant){
    Run run = new Run(participant, this.round, this);
    runs.add(run);
  }

  public void proceedRoundAndDropParticipants() throws IllegalStateException {
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

    this.round = nextRound;
    top.forEach(run -> this.addRun(run.getParticipant()));
  }

  public void proceedRun() throws IllegalStateException {
    if (this.currentRun == null) throw new IllegalStateException("Current run is null");
    this.currentRun.endRun();

    Optional<Run> nextRun = runs.stream()
        .filter(run -> run.getRound().equals(this.round))
        .filter(run -> !run.getUserStatus().equals(UserStatus.DONE)) // 아직 끝나지 않은 Run
        .findFirst();

    this.currentRun = nextRun.orElse(null);
  }
}
