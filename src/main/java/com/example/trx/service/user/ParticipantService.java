package com.example.trx.service.user;

import com.example.trx.domain.event.ContestEvent;
import com.example.trx.domain.event.exception.ContestEventNotFound;
import com.example.trx.domain.user.Participant;
import com.example.trx.repository.event.ContestEventRepository;
import com.example.trx.repository.user.ParticipantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ParticipantService {

  private final ParticipantRepository participantRepository;
  private final ContestEventRepository contestEventRepository;

  @Transactional
  public void addParticipant() {

  }

  @Transactional
  public void addParticipant(Long eventId, Long participantId) {
    ContestEvent contestEvent = contestEventRepository.findById(eventId).orElseThrow(() -> new ContestEventNotFound(eventId));
    Participant participant = participantRepository.findById(participantId).orElseThrow(() -> new IllegalStateException());//TODO
    participant.participate(contestEvent);
  }
}
