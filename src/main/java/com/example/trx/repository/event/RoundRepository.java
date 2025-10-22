package com.example.trx.repository.event;

import com.example.trx.domain.event.round.Round;
import com.example.trx.domain.event.round.RoundStatus;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoundRepository extends JpaRepository<Round, Long> {
  Optional<Round> findRoundByStatus(RoundStatus status);
}
