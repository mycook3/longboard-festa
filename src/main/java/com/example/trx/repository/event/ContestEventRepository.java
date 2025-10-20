package com.example.trx.repository.event;

import com.example.trx.domain.event.ContestEvent;
import com.example.trx.domain.event.ContestEventStatus;
import com.example.trx.domain.event.DisciplineCode;
import com.example.trx.domain.event.Division;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContestEventRepository extends JpaRepository<ContestEvent, Long> {
  Optional<ContestEvent> findContestEventByDivisionAndDisciplineCode( Division division, DisciplineCode disciplineCode);
  List<ContestEvent> findContestEventByContestEventStatus(ContestEventStatus contestEventStatus);
}
