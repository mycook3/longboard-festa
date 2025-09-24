package com.example.trx.service;

import com.example.trx.domain.judge.Judge;
import com.example.trx.domain.run.Run;
import com.example.trx.domain.score.ScoreTotal;
import com.example.trx.domain.user.Participant;
import com.example.trx.repository.JudgeRepository;
import com.example.trx.repository.ParticipantRepository;
import com.example.trx.repository.RunRepository;
import java.math.BigDecimal;
import java.util.NoSuchElementException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class JudgeService {

  private JudgeRepository judgeRepository;
  private ParticipantRepository participantRepository;
  private RunRepository runRepository;

  @Transactional
  public void submitScore(Long judgeId, Long runId, BigDecimal totalScore, String breakdownJson) {
    Judge judge = judgeRepository.findById(judgeId).orElseThrow(() -> new NoSuchElementException("Judge not found"));
    Run run = runRepository.findById(runId).orElseThrow(() -> new NoSuchElementException("Run not found"));

    judge.submitScore(run, totalScore, breakdownJson);
  }

  //TODO
  public void addJudge() {




  }

  //TODO
  public void deleteJudge() {



  }

  //TODO
  public void modifyJudge() {



  }



}
