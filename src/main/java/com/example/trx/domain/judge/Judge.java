package com.example.trx.domain.judge;

import com.example.trx.domain.event.ContestEvent;
import com.example.trx.domain.run.Run;
import com.example.trx.domain.score.ScoreTotal;
import com.example.trx.support.util.BaseTimeEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Judge extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contest_event_id", nullable = false)
    private ContestEvent contestEvent;

    @Column(name = "judge_number", nullable = false)
    private Integer judgeNumber;

    @Column(name = "name", nullable = false, length = 64)
    private String name;

    // 관계: Judge 1 : N ScoreTotal
    @OneToMany(mappedBy = "judge", cascade = CascadeType.ALL, orphanRemoval = false)
    @Builder.Default
    private List<ScoreTotal> scores = new ArrayList<>();

    // 채점
    public void submitScore(Run run, BigDecimal score, String breakDownJson) {
      ScoreTotal submitted = new ScoreTotal(run, this, score, breakDownJson);
      scores.add(submitted);
    }
}