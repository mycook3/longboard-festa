package com.example.trx.repository;

import com.example.trx.domain.judge.Judge;
import com.example.trx.domain.user.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ParticipantRepository extends JpaRepository<Participant, Long> {

}
