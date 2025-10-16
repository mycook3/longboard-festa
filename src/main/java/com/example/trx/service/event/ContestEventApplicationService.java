package com.example.trx.service.event;

import com.example.trx.apis.event.dto.response.ContestEventResponse;
import com.example.trx.apis.event.dto.request.EditScoreRequest;
import com.example.trx.apis.event.dto.response.RunResponse;
import com.example.trx.apis.event.dto.response.ScoreResponse;
import com.example.trx.apis.event.dto.request.SubmitScoreRequest;
import com.example.trx.domain.event.ContestEvent;
import com.example.trx.domain.run.Run;
import com.example.trx.domain.score.ScoreTotal;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional //DTO 매핑을 위한 lazy fetch용 Transaction 유지(osiv off)
public class ContestEventApplicationService {

  private final ContestEventDomainService contestEventDomainService;

  public ContestEventResponse getContestEventById(Long contestEventId) {
    ContestEvent contestEvent  = contestEventDomainService.getContestEventById(contestEventId);
    return makeContestEventResponse(contestEvent);
  }

  public void initContest(Long eventId) {
    contestEventDomainService.initContest(eventId);
  }

  public void startContestEvent(Long eventId) {
    contestEventDomainService.startContestEvent(eventId);
  }

  public void proceedRun(Long eventId) {
    contestEventDomainService.proceedRun(eventId);
  }

  public void proceedRound(Long eventId) {
    contestEventDomainService.proceedRound(eventId);
  }

  public void submitScore(Long runId, SubmitScoreRequest request) {
    contestEventDomainService.submitScore(runId, request.getJudgeId(), request.getScoreTotal(), request.getBreakdownJson());
  }

  public void editScore(Long scoreId, EditScoreRequest request) {
    contestEventDomainService.editScore(scoreId, request.getScoreTotal(), request.getBreakdownJson(), request.getEditedBy(), request.getEditReason());
  }

  private ContestEventResponse makeContestEventResponse(ContestEvent contestEvent) {
    return ContestEventResponse.builder()
        .id(contestEvent.getId())
        .eventName(contestEvent.getDisciplineCode().name())
        .status(contestEvent.getContestEventStatus().name())
        .division(contestEvent.getDivision().name())
        .currentRound(contestEvent.getCurrentRound().getName())
        .runs(makeRunResponseList(contestEvent.getCurrentRound().getRuns()))
        .build();
  }

  private List<RunResponse> makeRunResponseList(List<Run> runs) {
    return runs.stream()
        .map(run ->
            RunResponse.builder()
                .id(run.getId())
                .participantName(run.getParticipant().getNameKr())
                .status(run.getUserStatus().name())
                .scores(makeScoreResponseList(run.getScores()))
                .build()
        )
        .toList();
  }

  private List<ScoreResponse> makeScoreResponseList(List<ScoreTotal> scores) {
    return scores.stream()
        .map(score ->
            ScoreResponse.builder()
                .id(score.getId())
                .score(score.getTotal())
                .judgeId(score.getJudge().getId())
                .judgeName(score.getJudge().getName())
                .build()
            )
        .toList();
  }
}
