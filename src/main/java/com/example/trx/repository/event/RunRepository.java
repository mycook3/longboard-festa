package com.example.trx.repository.event;

import com.example.trx.domain.event.round.run.Run;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RunRepository extends JpaRepository<Run, Long> {

}
