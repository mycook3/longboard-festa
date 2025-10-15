package com.example.trx.service.judge;

import com.example.trx.apis.admin.dto.AdminTokenResponse;
import com.example.trx.apis.judge.dto.JudgeCreateRequest;
import com.example.trx.apis.judge.dto.JudgeResponse;
import com.example.trx.apis.judge.dto.JudgeUpdateRequest;
import com.example.trx.domain.judge.Judge;
import com.example.trx.domain.judge.JudgeStatus;
import com.example.trx.domain.judge.exception.JudgeAlreadyExistsException;
import com.example.trx.domain.judge.exception.JudgeInvalidCredentialsException;
import com.example.trx.domain.judge.exception.JudgeNotFoundException;
import com.example.trx.domain.run.Run;
import com.example.trx.domain.run.exception.RunNotFoundException;
import com.example.trx.repository.event.ContestEventRepository;
import com.example.trx.repository.run.RunRepository;
import com.example.trx.repository.judge.JudgeRepository;
import com.example.trx.repository.run.RunRepository;
import com.example.trx.support.security.JwtTokenProvider;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class JudgeService {

    private final JudgeRepository judgeRepository;
    private final PasswordEncoder passwordEncoder;
    private final RunRepository runRepository;
    private final JwtTokenProvider jwtTokenProvider;

    @Transactional
    public JudgeResponse createJudge(JudgeCreateRequest request) {
        if (judgeRepository.existsByUsername(request.getUsername())) {
            throw new JudgeAlreadyExistsException(request.getUsername());
        }

        Judge judge = Judge.builder()
            .name(request.getName())
            .username(request.getUsername())
            .password(passwordEncoder.encode(request.getPassword()))
            .judgeNumber(request.getJudgeNumber())
            .status(JudgeStatus.ACTIVE)
            .build();

        Judge saved = judgeRepository.save(judge);

        return JudgeResponse.builder()
            .id(saved.getId())
            .name(saved.getName())
            .username(saved.getUsername())
            .judgeNumber(saved.getJudgeNumber())
            .status(saved.getStatus())
            .build();
    }

    @Transactional(readOnly = true)
    public List<JudgeResponse> getJudges() {
        return judgeRepository.findAllByDeletedFalse().stream()
            .map(judge -> JudgeResponse.builder()
                .id(judge.getId())
                .name(judge.getName())
                .username(judge.getUsername())
                .judgeNumber(judge.getJudgeNumber())
                .status(judge.getStatus())
                .build())
            .collect(Collectors.toList());
    }

    @Transactional
    public JudgeResponse updateJudge(Long judgeId, JudgeUpdateRequest request) {
        Judge judge = judgeRepository.findByIdAndDeletedFalse(judgeId)
            .orElseThrow(() -> new JudgeNotFoundException(judgeId));

        judge.setName(request.getName());
        judge.setStatus(request.getStatus());

        return JudgeResponse.builder()
            .id(judge.getId())
            .name(judge.getName())
            .username(judge.getUsername())
            .judgeNumber(judge.getJudgeNumber())
            .status(judge.getStatus())
            .build();
    }

    @Transactional
    public void deactivateJudge(Long judgeId) {
        Judge judge = judgeRepository.findByIdAndDeletedFalse(judgeId)
            .orElseThrow(() -> new JudgeNotFoundException(judgeId));
        judge.setStatus(JudgeStatus.INACTIVE);
        judge.markDeleted();
    }

    @Transactional(readOnly = true)
    public AdminTokenResponse loginJudge(String username, String rawPassword) {
        Judge judge = judgeRepository.findByUsernameAndDeletedFalse(username)
            .orElseThrow(() -> new JudgeNotFoundException(username));

        if (!passwordEncoder.matches(rawPassword, judge.getPassword())) {
            throw new JudgeInvalidCredentialsException();
        }

        String token = jwtTokenProvider.generateToken(judge.getUsername(), List.of("ROLE_JUDGE"));

        return AdminTokenResponse.builder()
            .token(token)
            .tokenType(AdminTokenResponse.TokenType.BEARER)
            .build();
    }

    @Transactional
    public void submitScore(Long runId, Long judgeId, BigDecimal score, String breakdownJson) {
      Run run = runRepository.findById(runId).orElseThrow(() -> new RunNotFoundException(runId));
      Judge judge = judgeRepository.findById(judgeId).orElseThrow(() -> new JudgeNotFoundException(judgeId));
      judge.submitScore(run, score, breakdownJson);
    }
}
