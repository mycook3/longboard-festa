package com.example.trx.domain.score;

import com.example.trx.domain.judge.Judge;
import com.example.trx.domain.run.Run;
import com.example.trx.support.util.BaseTimeEntity;
import jakarta.persistence.*;
import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.Comment;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class ScoreTotal extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // 무엇에 대한 점수인가? -> Run
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "run_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_score_run"))
    private Run run;

    // 누가 매겼는가? -> Judge
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "judge_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_score_judge"))
    private Judge judge;

    @Comment("총점")
    @Column(name = "total", nullable = false, precision = 8, scale = 2)
    private BigDecimal total;

    // 세부사항 JSON (PostgreSQL jsonb)
    @Comment("세부사항(JSON): criteria/penalties/raw/computed/rule_ver 등")
    @Column(name = "breakdown_json", columnDefinition = "jsonb")
    private String breakdownJson;

    // 운영용(선택)
    @Column(name = "is_locked", nullable = false)
    private boolean locked = false;

    @Column(name = "edited_by", length = 64)
    private String editedBy;

    @Column(name = "edit_reason", length = 255)
    private String editReason;
}