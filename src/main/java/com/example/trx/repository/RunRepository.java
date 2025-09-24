package com.example.trx.repository;

import com.example.trx.domain.run.Run;
import com.example.trx.domain.user.Participant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RunRepository extends JpaRepository<Run, Long> {

}
