package com.example.trx.service.user;

import com.example.trx.apis.user.dto.ParticipantCreateRequest;
import com.example.trx.domain.event.ContestEvent;
import com.example.trx.domain.event.DisciplineCode;
import com.example.trx.domain.event.Division;
import com.example.trx.domain.event.exception.ContestEventNotFound;
import com.example.trx.domain.user.Gender;
import com.example.trx.domain.user.Participant;
import com.example.trx.domain.user.UserStatus;
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
  public Participant createParticipantAndParticipate(ParticipantCreateRequest request) {
    Gender gender = Gender.valueOf(request.getGender());
    Division division = Division.valueOf(request.getDivision());
    Integer bibNumber = Math.toIntExact(participantRepository.count() + 1);

    Participant participant = Participant.builder()
        .nameKr(request.getNameKr())
        .bibNumber(bibNumber)
        .birth(request.getBirth())
        .phone(request.getPhone())
        .emergencyContact(request.getEmergencyContact())
        .email(request.getEmail())
        .gender(gender)
        .residence(request.getResidence())
        .division(division)
        .memo(request.getMemo())
        .oneLiner(request.getOneLiner())
        .userStatus(UserStatus.WAITING)
        .build();

    for (String eventToParticipate : request.getEventToParticipate()) {
      DisciplineCode disciplineCode = DisciplineCode.valueOf(eventToParticipate);
      ContestEvent contestEvent = contestEventRepository
          .findContestEventByDivisionAndDisciplineCode(division, disciplineCode)
          .orElseThrow(() -> new ContestEventNotFound(division, disciplineCode));

      participant.participate(contestEvent);
    }

    return participantRepository.save(participant);
  }
}
