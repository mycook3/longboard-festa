package com.example.trx.domain.run;

import com.example.trx.domain.event.ContestEvent;
import com.example.trx.domain.event.Round;
import com.example.trx.domain.score.ScoreTotal;
import com.example.trx.domain.event.DisciplineCode;
import com.example.trx.domain.user.Participant;
import com.example.trx.domain.user.UserStatus;
import com.example.trx.support.util.BaseTimeEntity;
import jakarta.persistence.*;
import java.util.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 각 참가자들의 퍼포먼스 1회
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Run extends BaseTimeEntity {

    public Run(Participant participant, Round round, ContestEvent contestEvent) {
      this.participant = participant;
      this.round = round;
      this.contestEvent = contestEvent;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 참가자
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "participant_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_run_participant"))
    private Participant participant;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "contest_event_id", nullable = false)
    private ContestEvent contestEvent;

    // 라운드 번호
    @Enumerated(EnumType.STRING)
    @Column(name = "round", nullable = false)
    private Round round;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    private UserStatus userStatus = UserStatus.WAITING;

    // 관계: Run 1 : N ScoreTotal
    @OneToMany(mappedBy = "run", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<ScoreTotal> scores = new ArrayList<>();

    // 편의 메서드
    public void addScore(ScoreTotal score) {
        scores.add(score);
        score.setRun(this);
    }

    public void endRun() {
      if (this.scores.size() == this.contestEvent.getJudges().size()) {
        userStatus = UserStatus.DONE;
      }
      //TODO 그 외의 경우 예외처x`리
    }
}