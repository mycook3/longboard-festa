package com.example.trx.domain.event.round.match;

import com.example.trx.domain.event.round.Round;
import com.example.trx.domain.event.round.run.Run;
import com.example.trx.domain.user.Participant;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Match {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  private Round round;

  @ManyToOne(fetch = FetchType.LAZY)
  private Participant participant1;

  @ManyToOne(fetch = FetchType.LAZY)
  private Participant participant2;

  @ManyToOne(fetch = FetchType.LAZY)
  private Participant winner;//동점자 처리가 필요한 경우 수동으로 정해야합니다.

  @OneToMany (mappedBy = "match", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
  @Builder.Default
  private List<Run> runs = new ArrayList<>();

  @Enumerated(EnumType.STRING)
  @Builder.Default
  private MatchType matchType = MatchType.NORMAL;

  public void addRun(Run run) {
    run.setMatch(this);
    runs.add(run);
  }

  public List<Participant> getWinners() {
    List<Participant> winners = new ArrayList<>();

    if (matchType == MatchType.BYE) {//부전승 처리된 경우 모두를 다음 라운드로 진행시킵니다
      if (participant1 != null) winners.add(participant1);
      if (participant2 != null) winners.add(participant2);
      return winners;
    }
    else if (winner != null) {
      winners.add(winner);
      return winners;
    }

    BigDecimal bestScore1 = runs.stream()
        .filter(run -> run.getParticipant().equals(participant1))
        .map(Run::getScore)
        .max(Comparator.naturalOrder())
        .orElse(BigDecimal.ZERO);

    BigDecimal bestScore2 = runs.stream()
        .filter(run -> run.getParticipant().equals(participant2))
        .map(Run::getScore)
        .max(Comparator.naturalOrder())
        .orElse(BigDecimal.ZERO);

    if (bestScore1.compareTo(bestScore2) > 0) {
      winners.add(participant1);
      this.winner = participant1;
    }
    else if (bestScore2.compareTo(bestScore1) > 0) {
      winners.add(participant2);
      this.winner = participant2;
    }
    else throw new IllegalStateException("동점자 발생");

    return winners;
  }
}
