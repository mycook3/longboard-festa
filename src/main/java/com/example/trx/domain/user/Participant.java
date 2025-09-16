package com.example.trx.domain.user;

import com.example.trx.domain.run.Run;
import com.example.trx.support.util.BaseTimeEntity;
import jakarta.persistence.*;
import java.time.LocalDate;
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
public class Participant extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Comment("이름(KR)")
    @Column(name = "name_kr", nullable = false, length = 64)
    private String nameKr;

    @Comment("참가번호(접수순 고유번호)")
    @Column(name = "bib_number", unique = true)
    private Integer bibNumber;

    @Comment("생년월일")
    @Column(name = "birth")
    private LocalDate birth;

    @Comment("연락처(전화번호)")
    @Column(name = "phone", length = 32)
    private String phone;

    @Comment("이메일")
    @Column(name = "email", length = 100)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = 16)
    private Gender gender;

    @Comment("거주지")
    @Column(name = "residence", length = 100)
    private String residence;

    @Enumerated(EnumType.STRING)
    @Column(name = "division", nullable = false, length = 32)
    private Division division;

    @Comment("참가 한마디")
    @Column(name = "one_liner", length = 255)
    private String oneLiner;

    @Comment("비상연락처")
    @Column(name = "emergency_contact", length = 100)
    private String emergencyContact;

    @Comment("메모")
    @Column(name = "memo")
    private String memo;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    private UserStatus userStatus = UserStatus.WAITING;

    // 관계: Participant 1 : N Run
    @OneToMany(mappedBy = "participant", cascade = CascadeType.ALL, orphanRemoval = false)
    @Builder.Default
    private List<Run> runs = new ArrayList<>();
}