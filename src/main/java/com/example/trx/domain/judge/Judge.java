package com.example.trx.domain.judge;

import com.example.trx.domain.event.ContestEvent;
import com.example.trx.domain.event.DisciplineCode;
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
    @JoinColumn(name = "contest_event_id")
    private ContestEvent contestEvent;

    @Column(name = "judge_number", nullable = false)
    private Integer judgeNumber;

    @Column(name = "name", nullable = false, length = 64)
    private String name;

    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    @Column(name = "password", nullable = false, length = 255)
    private String password;

    @Enumerated(EnumType.STRING)
    @Column(name = "discipline_code", nullable = false, length = 32)
    private DisciplineCode disciplineCode;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    @Builder.Default
    private JudgeStatus status = JudgeStatus.ACTIVE;

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
