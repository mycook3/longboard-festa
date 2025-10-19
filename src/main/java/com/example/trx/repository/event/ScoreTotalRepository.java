package com.example.trx.repository.event;

import com.example.trx.domain.event.round.run.score.ScoreTotal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScoreTotalRepository extends JpaRepository<ScoreTotal, Long> {

}