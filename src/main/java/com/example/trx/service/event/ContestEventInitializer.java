package com.example.trx.service.event;

import com.example.trx.domain.event.ContestEvent;
import com.example.trx.domain.event.DisciplineCode;
import com.example.trx.domain.event.Division;
import com.example.trx.domain.event.RoundProgressionType;
import com.example.trx.domain.event.exception.ContestEventNotFound;
import com.example.trx.repository.event.ContestEventRepository;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ContestEventInitializer implements ApplicationRunner {

  private final ContestEventRepository contestEventRepository;
  private final ContestEventDomainService domainService;

  @Override
  @Transactional
  public void run(ApplicationArguments args) throws Exception {
    for (Division division : Division.values()) {
      for (DisciplineCode disciplineCode : DisciplineCode.values()) {
        ContestEvent contestEvent = contestEventRepository.findContestEventByDivisionAndDisciplineCode(division, disciplineCode).orElse(null);

        if (contestEvent == null) {
          contestEventRepository.save(
              ContestEvent.builder()
              .division(division)
              .disciplineCode(disciplineCode)
              .build()
          );
        }
      }
    }

    initFreeStyleRounds();
    initSlalomRounds();
    initDancingRounds();
  }

  private void initFreeStyleRounds() {
      Map<String, Integer> beginner = Map.copyOf(new LinkedHashMap<>() {{
        put("qualifier", 10);
        put("semifinal", 5);
        put("final", 3);
      }});

      Map<String, Integer> open = Map.copyOf(new LinkedHashMap<>() {{
        put("qualifier", 13);
        put("semifinal", 7);
        put("final", 4);
      }});

      Map<String, Integer> pro = Map.copyOf(new LinkedHashMap<>() {{
        put("qualifier", 21);
        put("semifinal", 11);
        put("final", 4);
      }});

    for (Division division : Division.values()) {
      ContestEvent cev = contestEventRepository.findContestEventByDivisionAndDisciplineCode(division, DisciplineCode.FREESTYLE)
          .orElseThrow(() -> new ContestEventNotFound(division, DisciplineCode.FREESTYLE));

      switch (division) {
        case BEGINNER -> addRounds(cev, beginner, 1);
        case OPEN -> addRounds(cev, open, 1);
        case PRO -> addRounds(cev, pro, 1);
      }
    }
  }

  private void initSlalomRounds() {
    Map<String, Integer> beginner = Map.copyOf(new LinkedHashMap<>() {{
      put("round16", 16);
      put("round8", 8);
      put("semifinal", 4);
      put("final", 2);
    }});

    Map<String, Integer> open = Map.copyOf(new LinkedHashMap<>() {{
      put("round8", 8);
      put("semifinal", 4);
      put("final", 2);
    }});

    Map<String, Integer> pro = Map.copyOf(new LinkedHashMap<>() {{
      put("round8", 8);
      put("semifinal", 4);
      put("final", 2);
    }});

    for (Division division : Division.values()) {
      ContestEvent cev = contestEventRepository.findContestEventByDivisionAndDisciplineCode(division, DisciplineCode.SLALOM)
          .orElseThrow(() -> new ContestEventNotFound(division, DisciplineCode.SLALOM));

      cev.setProgressionType(RoundProgressionType.TOURNAMENT);

      switch (division) {
        case BEGINNER -> addRounds(cev, beginner, 1);
        case OPEN -> addRounds(cev, open, 1);
        case PRO -> addRounds(cev, pro, 1);
      }
    }
  }

  private void initDancingRounds() {
    Map<String, Integer> beginner = Map.copyOf(new LinkedHashMap<>() {{
      put("final", 5);
    }});

    Map<String, Integer> open = Map.copyOf(new LinkedHashMap<>() {{
      put("final", 7);
    }});

    Map<String, Integer> pro = Map.copyOf(new LinkedHashMap<>() {{
      put("final", 12);
    }});

    for (Division division : Division.values()) {
      ContestEvent cev = contestEventRepository.findContestEventByDivisionAndDisciplineCode(division, DisciplineCode.LONGBOARD_DANCING)
          .orElseThrow(() -> new ContestEventNotFound(division, DisciplineCode.LONGBOARD_DANCING));

      switch (division) {
        case BEGINNER -> addRounds(cev, beginner, 2);
        case OPEN -> addRounds(cev, open, 2);
        case PRO -> addRounds(cev, pro, 2);
      }
    }
  }

  private void addRounds(ContestEvent contestEvent, Map<String, Integer> rounds, Integer runPerParticipant) {
    rounds.forEach((name, limit) -> domainService.addRound(contestEvent.getId(), name, limit, runPerParticipant));
  }
}
