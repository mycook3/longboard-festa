package com.example.trx.domain.user;

import com.example.trx.domain.event.ContestEvent;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor
public class Participation {//초기 등록 현황을 기록

  @Id
  @GeneratedValue
  private Long id;

  @Enumerated(EnumType.STRING)
  private ParticipationStatus status;

  @ManyToOne
  @JoinColumn(name = "participant_id")
  private Participant participant;

  @ManyToOne
  @JoinColumn(name = "contest_event_id")
  private ContestEvent contestEvent;

  public Participation(Participant participant, ContestEvent contestEvent) {
    this.participant = participant;
    this.contestEvent = contestEvent;
    this.status = ParticipationStatus.ACTIVE;
  }

  public void drop() {
    this.status = ParticipationStatus.DROPPED;
  }
}
