package com.example.trx.repository.run;

import com.example.trx.domain.run.Run;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RunRepository extends JpaRepository<Run, Long> {

}
