package com.example.trx.domain.run;

import com.example.trx.domain.score.ScoreTotal;
import com.example.trx.domain.user.DisciplineCode;
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

    // 참가자
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "participant_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_run_participant"))
    private Participant participant;

    // 종목 코드
    @Enumerated(EnumType.STRING)
    @Column(name = "discipline_code", nullable = false, length = 32)
    private DisciplineCode disciplineCode;

    // 라운드 번호 (예: 1=예선, 2=결선)
    @Column(name = "round_no", nullable = false)
    private Integer roundNo;

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
}