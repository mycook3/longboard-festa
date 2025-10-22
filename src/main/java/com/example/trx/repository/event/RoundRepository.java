package com.example.trx.repository.event;

import com.example.trx.domain.event.round.Round;
import com.example.trx.domain.event.round.RoundStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoundRepository extends JpaRepository<Round, Long> {
  List<Round> findRoundsByStatus(RoundStatus status);
}
