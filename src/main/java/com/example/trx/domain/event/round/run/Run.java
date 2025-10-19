package com.example.trx.domain.event.round.run;

import com.example.trx.domain.event.round.match.Match;
import com.example.trx.domain.event.round.Round;
import com.example.trx.domain.event.round.run.score.ScoreStatus;
import com.example.trx.domain.event.round.run.score.ScoreTotal;
import com.example.trx.domain.user.Participant;
import com.example.trx.support.util.BaseTimeEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * 각 참가자들의 퍼포먼스 1회
 */
@Slf4j
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Run extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //라운드 내에서 한 참가자의 몇 번째 시도인지
    @Column(nullable = false)
    @Builder.Default
    private Integer attemptNumber = 1;

    // 참가자
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "participant_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_run_participant"))
    private Participant participant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "round_id", nullable = false)
    private Round round;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "match_id", nullable = true)
    @Builder.Default
    private Match match = null;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    @Builder.Default
    private RunStatus status = RunStatus.WAITING;

    // 관계: Run 1 : N ScoreTotal
    @OneToMany(mappedBy = "run", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ScoreTotal> scores = new ArrayList<>();

    // 편의 메서드
    public void addScore(ScoreTotal score) {
      score.setRun(this);
      score.getJudge().addScore(score);
      scores.add(score);
    }

    public void markAsDone() {
        this.status = RunStatus.DONE;
    }

    public void markAsOngoing() {
        this.status = RunStatus.ONGOING;
    }

    public boolean canBeCompleted(int judgeCount) {
      long submittedCount = scores.stream().filter(score -> score.getStatus() == ScoreStatus.SUBMITTED).count();
      return submittedCount == judgeCount;
    }

    public BigDecimal getScore() {
      List<BigDecimal> scores = this.scores
          .stream()
          .filter(score -> score.getStatus() == ScoreStatus.SUBMITTED)
          .map(ScoreTotal::getTotal)
          .sorted()
          .toList();

      if (scores.size() > 2) {//3인 이상. 최고점/최저점을 제외
        return scores.subList(1, scores.size() - 1)
            .stream()
            .reduce(BigDecimal.ZERO, BigDecimal::add);
      }

      return scores.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}