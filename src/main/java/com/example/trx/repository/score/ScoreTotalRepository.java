package com.example.trx.repository.score;

import com.example.trx.domain.score.ScoreTotal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScoreTotalRepository extends JpaRepository<ScoreTotal, Long> {

}