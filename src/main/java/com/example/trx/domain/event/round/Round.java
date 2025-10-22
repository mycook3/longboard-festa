package com.example.trx.domain.event.round;

import com.example.trx.domain.event.ContestEvent;
import com.example.trx.domain.event.RoundProgressionType;
import com.example.trx.domain.judge.Judge;
import com.example.trx.domain.user.Participant;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorColumn;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.ManyToOne;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Entity
@DiscriminatorColumn(name = "progression_type")
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@NoArgsConstructor
@AllArgsConstructor
@Data
@SuperBuilder(toBuilder = true)
public abstract class Round {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  protected Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  protected ContestEvent contestEvent;

  @Enumerated(value = EnumType.STRING)
  @Column(name = "progression_type", updatable = false, insertable = false)
  protected RoundProgressionType progressionType;

  protected String name;
  protected Integer participantLimit;

  protected Integer runsPerParticipant = 1;

  @Enumerated(EnumType.STRING)
  protected RoundStatus status = RoundStatus.BEFORE;

  public void markAsInProgress() {
    this.status = RoundStatus.IN_PROGRESS;
  }

  public void markAsCompleted() {
    this.status = RoundStatus.COMPLETED;
  }

  public abstract void addParticipants(List<Participant> participants);
  public abstract void start(List<Judge> judges);
  public abstract boolean canBeCompleted();
  public abstract void proceed(int activeJudgesCount);
  public abstract List<Participant> getSurvivors(Round nextRound);
}
