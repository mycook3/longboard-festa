package com.example.trx.service.event;

import com.example.trx.domain.event.ContestEvent;
import com.example.trx.domain.event.DisciplineCode;
import com.example.trx.domain.event.Division;
import com.example.trx.domain.event.RoundProgressionType;
import com.example.trx.domain.event.exception.ContestEventNotFound;
import com.example.trx.repository.event.ContestEventRepository;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.util.Pair;
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
      List<Map.Entry<String, Integer>> beginner = List.of (
        Map.entry("qualifier", 10),
        Map.entry("semifinal", 5),
        Map.entry("final", 3)
      );

      List<Map.Entry<String, Integer>> open= List.of(
        Map.entry("qualifier", 13),
        Map.entry("semifinal", 7),
        Map.entry("final", 4)
      );

    List<Map.Entry<String, Integer>> pro= List.of(
        Map.entry("qualifier", 21),
        Map.entry("semifinal", 11),
        Map.entry("final", 4)
      );

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
    List<Map.Entry<String, Integer>> beginner = List.of(
      Map.entry("round16", 16),
      Map.entry("round8", 8),
      Map.entry("semifinal", 4),
      Map.entry("final", 2)
    );

    List<Map.Entry<String, Integer>> open = List.of(
      Map.entry("round8", 8),
      Map.entry("semifinal", 4),
      Map.entry("final", 2)
    );

    List<Map.Entry<String, Integer>> pro = List.of(
      Map.entry("round8", 8),
      Map.entry("semifinal", 4),
      Map.entry("final", 2)
    );

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
    List<Map.Entry<String, Integer>> beginner = List.of(
      Map.entry("final", 5)
    );

    List<Map.Entry<String, Integer>> open = List.of(
      Map.entry("final", 7)
    );

    List<Map.Entry<String, Integer>> pro = List.of(
      Map.entry("final", 12)
    );

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

  private void addRounds(ContestEvent contestEvent, List<Map.Entry<String, Integer>> rounds, Integer runPerParticipant) {
    rounds.forEach(round -> {
      String name = round.getKey();
      Integer limit = round.getValue();
      domainService.addRound(contestEvent.getId(), name, limit, runPerParticipant);
    });
  }
}
