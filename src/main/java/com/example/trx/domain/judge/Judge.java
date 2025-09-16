package com.example.trx.domain.judge;

import com.example.trx.domain.score.ScoreTotal;
import com.example.trx.support.util.BaseTimeEntity;
import jakarta.persistence.*;
import java.util.*;
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
public class Judge extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "judge_number", nullable = false)
    private Integer judgeNumber;

    @Column(name = "name", nullable = false, length = 64)
    private String name;

    // 관계: Judge 1 : N ScoreTotal
    @OneToMany(mappedBy = "judge", cascade = CascadeType.ALL, orphanRemoval = false)
    @Builder.Default
    private List<ScoreTotal> scores = new ArrayList<>();
}