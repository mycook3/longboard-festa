package com.example.trx.repository.user;

import com.example.trx.domain.event.ContestEvent;
import com.example.trx.domain.user.Participant;
import com.example.trx.domain.user.Participation;
import com.example.trx.domain.user.ParticipationStatus;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParticipationRepository extends JpaRepository<Participation, Long> {

  List<Participation> findByContestEventAndStatus(ContestEvent contestEvent, ParticipationStatus status);

}
