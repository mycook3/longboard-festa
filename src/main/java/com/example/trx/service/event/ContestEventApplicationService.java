package com.example.trx.service.event;

import com.example.trx.apis.event.dto.request.AddRoundRequest;
import com.example.trx.apis.event.dto.request.EditRoundRequest;
import com.example.trx.apis.event.dto.response.ContestEventResponse;
import com.example.trx.apis.event.dto.request.EditScoreRequest;
import com.example.trx.apis.event.dto.response.MatchResponse;
import com.example.trx.apis.event.dto.response.RoundResponse;
import com.example.trx.apis.event.dto.response.RunResponse;
import com.example.trx.apis.event.dto.response.ScoreResponse;
import com.example.trx.apis.event.dto.request.SubmitScoreRequest;
import com.example.trx.domain.event.ContestEvent;
import com.example.trx.domain.event.RoundProgressionType;
import com.example.trx.domain.event.round.Round;
import com.example.trx.domain.event.round.match.Match;
import com.example.trx.domain.event.round.run.Run;
import com.example.trx.domain.event.round.run.score.ScoreTotal;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ContestEventApplicationService {

  private final ContestEventDomainService domainService;

  @Transactional //DTO 매핑을 위한 lazy fetch용 Transaction 유지(osiv off)
  public ContestEventResponse getContestEventById(Long contestEventId) {
    ContestEvent contestEvent  = domainService.getContestEventById(contestEventId);
    return makeContestEventResponse(contestEvent, Collections.emptyList());
  }

  @Transactional //DTO 매핑을 위한 lazy fetch용 Transaction 유지(osiv off)
  public ContestEventResponse getContestEventByEventNameAndDivision(String eventName, String division, List<String> roundNames) {
    ContestEvent contestEvent  = domainService.getContestEventByDivisionAndDisciplineCode(eventName, division);
    return makeContestEventResponse(contestEvent, roundNames);
  }

  //Transaction 동작 방식에 따라 Transactional을 붙이면 안됩니다
  public void startContestEvent(Long eventId) {
    domainService.initContest(eventId);
    domainService.startCurrentRound(eventId);
  }

  public void endContestEvent(Long eventId) {
    domainService.endContestEvent(eventId);
  }

  public void proceedRun(Long eventId) {
    domainService.proceedRun(eventId);
  }

  public void proceedRound(Long eventId) {
    domainService.proceedRound(eventId);
  }

  public void addRound(Long contestId, AddRoundRequest request){
    domainService.addRound(contestId, request.getRoundName(), request.getLimit(), request.getRunPerParticipant());
  }

  public void editRound(Long roundId, EditRoundRequest request){
    domainService.editRound(roundId, request.getRoundName(), request.getLimit());
  }

  public void makeMatchBye(Long matchId) {
    domainService.makeMatchBye(matchId);
  }

  public void makeManualParticipant(Long matchId, Long participantId) {
    domainService.makeManualParticipant(matchId, participantId);
  }

  public void deleteRound(Long roundId) {
    domainService.deleteRound(roundId);
  }

  public void submitScore(Long runId, SubmitScoreRequest request) {
    domainService.submitScore(runId, request.getJudgeId(), request.getScoreTotal(), request.getBreakdownJson());
  }

  public void editScore(Long scoreId, EditScoreRequest request) {
    domainService.editScore(scoreId, request.getScoreTotal(), request.getBreakdownJson(), request.getEditedBy(), request.getEditReason());
  }

  private ContestEventResponse makeContestEventResponse(ContestEvent contestEvent, List<String> roundNames) {
    return ContestEventResponse.builder()
        .id(contestEvent.getId())
        .eventName(contestEvent.getDisciplineCode().name())
        .status(contestEvent.getContestEventStatus().name())
        .roundProgressionType(contestEvent.getProgressionType().name())
        .division(contestEvent.getDivision().name())
        .currentRound(contestEvent.getCurrentRound() != null
            ? contestEvent.getCurrentRound().getName()
            : "")
        .rounds(makeRoundResponseList(contestEvent.getRounds(), roundNames))
        .build();
  }

  private List<RoundResponse> makeRoundResponseList(List<Round> rounds, List<String> roundNames) {
    if (rounds.isEmpty()) return Collections.emptyList();

    boolean isTournament = rounds.get(0).getContestEvent().getProgressionType() == RoundProgressionType.TOURNAMENT;

    return rounds.stream()
        .filter(
            round -> roundNames.isEmpty() ||
            roundNames.contains(round.getName())
        )
        .map( round ->
            RoundResponse.builder()
                .id(round.getId())
                .name(round.getName())
                .participantLimit(round.getParticipantLimit())
                .status(round.getStatus().name())
                .currentRunId(round.getCurrentRun() != null
                    ? round.getCurrentRun().getId()
                    : null
                )
                .matches(isTournament ? makeMatchResponseList(round.getMatches()) : null)
                .runs(!isTournament ? makeRunResponseList(round.getRuns()) : null)
                .build()
        )
        .toList();
  }

  private List<MatchResponse> makeMatchResponseList(List<Match> matches) {
    return matches.stream()
        .map( match ->
            MatchResponse.builder()
                .id(match.getId())
                .type(match.getMatchType().name())
                .participant1Id(match.getParticipant1().getId())
                .participant1Name(match.getParticipant1().getNameKr())
                .participant2Id(match.getParticipant2() == null? null : match.getParticipant2().getId())
                .participant2Name(match.getParticipant2() == null? null : match.getParticipant2().getNameKr())
                .runs(makeRunResponseList(match.getRuns()))
                .build()
        )
        .toList();
  }

  private List<RunResponse> makeRunResponseList(List<Run> runs) {
    return runs.stream()
        .map(run ->
            RunResponse.builder()
                .id(run.getId())
                .attemptNumber(run.getAttemptNumber())
                .participantId(run.getParticipant().getId())
                .participantName(run.getParticipant().getNameKr())
                .status(run.getStatus().name())
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
                .status(score.getStatus().name())
                .judgeId(score.getJudge().getId())
                .judgeName(score.getJudge().getName())
                .breakdownJson(score.getBreakdownJson())
                .build()
            )
        .toList();
  }
}
