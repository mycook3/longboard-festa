package com.example.trx.domain.user;

import com.example.trx.domain.event.ContestEvent;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;

@Entity
@Getter
public class Participation {

  @Id
  @GeneratedValue
  private Long id;

  @ManyToOne
  @JoinColumn(name = "participation_id")
  private Participant participant;

  @ManyToOne
  @JoinColumn(name = "contest_event_id")
  private ContestEvent contestEvent;

}
