package com.example.trx.domain.user;

import com.example.trx.domain.event.ContestEvent;
import com.example.trx.domain.event.Division;
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

    // 이름
    @Column(name = "name_kr", nullable = false, length = 64)
    private String nameKr;

    // 참가순 번호
    @Column(name = "bib_number")
    private Integer bibNumber;

    // 생년월일
    @Column(name = "birth")
    private String birth;

    // 연락처
    @Column(name = "phone", length = 32)
    private String phone;

    // 비상 연락처
    @Column(name = "emergency_contact", length = 32)
    private String emergencyContact;

    // 이메일
    @Column(name = "email", length = 100)
    private String email;

    // 성별
    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = 16)
    private Gender gender;

    // 거주지
    @Column(name = "residence", length = 100)
    private String residence;

    // 수준
    @Column(name = "division", nullable = false, length = 32)
    private Division division;

    // 참가 종목
    @OneToMany(mappedBy = "participant", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Participation> participations = new ArrayList<>();

    // 한 마디 & 각오
    @Column(name = "one_liner", length = 255)
    private String oneLiner;

    // 메모
    @Column(name = "memo")
    private String memo;

    // 상태 << ?
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 16)
    @Builder.Default
    private UserStatus userStatus = UserStatus.WAITING;

    // 1회 시도 및 기록. 관계: Participant 1 : N Run
    @OneToMany(mappedBy = "participant", cascade = CascadeType.ALL, orphanRemoval = false, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Run> runs = new ArrayList<>();

    public void participate(ContestEvent event) {
      Participation participation = new Participation(this, event);
      event.getParticipations().add(participation);
      participations.add(participation);
    }
}