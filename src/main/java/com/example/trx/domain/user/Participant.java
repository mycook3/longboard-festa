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

    @Column(name = "name_kr", nullable = false, length = 64)
    private String nameKr;

    @Column(name = "bib_number")
    private Integer bibNumber;

    @Column(name = "birth")
    private LocalDate birth;

    @Column(name = "phone", length = 32)
    private String phone;

    @Column(name = "email", length = 100)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = 16)
    private Gender gender;

    @Column(name = "residence", length = 100)
    private String residence;

    @Column(name = "division", nullable = false, length = 32)
    private Division division;

    @Column(name = "one_liner", length = 255)
    private String oneLiner;

    @Column(name = "emergency_contact", length = 100)
    private String emergencyContact;

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