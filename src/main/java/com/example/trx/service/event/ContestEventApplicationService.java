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
import com.example.trx.domain.event.round.ScoreBasedRound;
import com.example.trx.domain.event.round.TournamentRound;
import com.example.trx.domain.event.round.match.Match;
import com.example.trx.domain.event.round.run.Run;
import com.example.trx.domain.event.round.run.score.ScoreTotal;
import com.example.trx.support.util.SseEvent;
import com.example.trx.support.util.SseEventType;
import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContestEventApplicationService {

  private final ApplicationEventPublisher eventPublisher;
  private final ContestEventDomainService domainService;

  @Transactional //DTO 매핑을 위한 lazy fetch용 Transaction 유지(osiv off)
  public ContestEventResponse getContestEventById(Long contestEventId) {
    ContestEvent contestEvent  = domainService.getContestEventById(contestEventId);
    return makeContestEventResponse(contestEvent, Collections.emptyList());
  }

  @Transactional //DTO 매핑을 위한 lazy fetch용 Transaction 유지(osiv off)
  public ContestEventResponse getContestEventByEventNameAndDivision(String eventName, String division, List<String> roundNames) {
    ContestEvent contestEvent  = domainService.getContestEventByDivisionAndDisciplineCode(division, eventName);
    return makeContestEventResponse(contestEvent, roundNames);
  }

  @Transactional
  public List<ContestEventResponse> getContestEventsRoundInProgress() {
    List<ContestEvent> currentEvents = domainService.getContestEventsRoundInProgress();
    return currentEvents.stream().map(contestEvent ->
        makeContestEventResponse(contestEvent,
            contestEvent.getCurrentRound() == null
                ? null
                : List.of(contestEvent.getCurrentRound().getName())))
        .toList();
  }

  //Transaction 동작 방식에 따라 Transactional을 붙이면 안됩니다
  public void startContestEvent(Long eventId) {
    domainService.initContest(eventId);
    domainService.startCurrentRound(eventId);

    eventPublisher.publishEvent(SseEvent.of(SseEventType.CONTEST_EVENT_STARTED, eventId));
  }

  public void endContestEvent(Long eventId) {
    domainService.endContestEvent(eventId);
    eventPublisher.publishEvent(SseEvent.of(SseEventType.CONTEST_EVENT_ENDED, eventId));
  }

  public void proceedRun(Long eventId) {
    domainService.proceedRun(eventId);
    eventPublisher.publishEvent(SseEvent.of(SseEventType.RUN_PROCEEDED, eventId));
  }

  public void proceedRound(Long eventId) {
    domainService.proceedRound(eventId);
    eventPublisher.publishEvent(SseEvent.of(SseEventType.ROUND_PROCEEDED, eventId));
  }

  public void startCurrentRound(Long eventId) {
    domainService.startCurrentRound(eventId);
    eventPublisher.publishEvent(SseEvent.of(SseEventType.ROUND_STARTED, eventId));
  }

  public void addRound(Long contestId, AddRoundRequest request){
    domainService.addRound(contestId, request.getRoundName(), request.getLimit(), request.getRunPerParticipant());
  }

  public void editRound(Long roundId, EditRoundRequest request){
    domainService.editRound(roundId, request.getRoundName(), request.getLimit());
  }

  public void makeMatchBye(Long matchId) {
    domainService.makeMatchBye(matchId);
    eventPublisher.publishEvent(SseEvent.of(SseEventType.MADE_BYE, matchId));
  }

  public void makeManualParticipant(Long matchId, Long participantId) {
    domainService.makeManualParticipant(matchId, participantId);
    eventPublisher.publishEvent(SseEvent.of(SseEventType.MADE_MANUAL_WINNER, matchId));
  }

  public void deleteRound(Long roundId) {
    domainService.deleteRound(roundId);
  }

  public void submitScore(Long runId, SubmitScoreRequest request) {
    domainService.submitScore(runId, request.getJudgeId(), request.getScoreTotal(), request.getBreakdownJson());
    eventPublisher.publishEvent(SseEvent.of(SseEventType.SCORE_SUBMITTED, runId));
  }

  public void editScore(Long scoreId, EditScoreRequest request) {
    domainService.editScore(scoreId, request.getScoreTotal(), request.getBreakdownJson(), request.getEditedBy(), request.getEditReason());
    eventPublisher.publishEvent(SseEvent.of(SseEventType.SCORE_EDITED, scoreId));
  }

  private ContestEventResponse makeContestEventResponse(ContestEvent contestEvent, List<String> roundNames) {
    if (contestEvent == null) return null;
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

    return rounds.stream()
        .filter(
            round -> roundNames == null || roundNames.isEmpty() ||
            roundNames.contains(round.getName())
        )
        .map( round -> {//TODO
          if (round instanceof TournamentRound tournamentRound) return toRoundResponse(tournamentRound);
          if (round instanceof ScoreBasedRound scoreBasedRound) return toRoundResponse(scoreBasedRound);
          log.warn("null round object {}", round.getId());
          return null;
        })
        .toList();
  }

  private List<MatchResponse> makeMatchResponseList(List<Match> matches) {
    if (matches == null) return Collections.emptyList();
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
    if (runs == null) return Collections.emptyList();

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
    if (scores == null) return Collections.emptyList();
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

  private RoundResponse toRoundResponse(ScoreBasedRound round) {
     return RoundResponse.builder()
        .id(round.getId())
        .name(round.getName())
        .participantLimit(round.getParticipantLimit())
        .status(round.getStatus().name())
        .currentRunId(
            round.getCurrentRun() != null
                ? round.getCurrentRun().getId()
                : null
        )
        .currentMatchId(null)
        .runs(makeRunResponseList(round.getRuns()))
        .build();
  }

  private RoundResponse toRoundResponse(TournamentRound round) {
      return RoundResponse.builder()
          .id(round.getId())
          .name(round.getName())
          .participantLimit(round.getParticipantLimit())
          .status(round.getStatus().name())
          .currentRunId(
              round.getCurrentRun() != null
                  ? round.getCurrentRun().getId()
                  : null
          )
          .currentMatchId(
              round.getCurrentMatch() != null
                ? round.getCurrentMatch().getId()
                : null
          )
          .matches(makeMatchResponseList(round.getMatches()))
          .build();
  }
}
