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
import com.example.trx.repository.judge.JudgeRepository;
import com.example.trx.support.security.JwtTokenProvider;
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
    private final JwtTokenProvider jwtTokenProvider;
    private static final int USERNAME_MAX_LENGTH = 50;

    @Transactional
    public JudgeResponse createJudge(JudgeCreateRequest request) {
        if (judgeRepository.existsByUsername(request.getUsername())) {
            throw new JudgeAlreadyExistsException(request.getUsername());
        }

        Judge judge = Judge.builder()
            .name(request.getName())
            .username(request.getUsername())
            .password(passwordEncoder.encode(request.getPassword()))
            .judgeNumber(0)
            .status(JudgeStatus.ACTIVE)
            .build();

        Judge saved = judgeRepository.save(judge);

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<JudgeResponse> getJudges() {
        return judgeRepository.findAllByDeletedFalse().stream()
            .map(this::toResponse)
            .collect(Collectors.toList());
    }

    @Transactional
    public JudgeResponse updateJudge(Long judgeId, JudgeUpdateRequest request) {
        Judge judge = judgeRepository.findByIdAndDeletedFalse(judgeId)
            .orElseThrow(() -> new JudgeNotFoundException(judgeId));

        judge.setName(request.getName());
        judge.setStatus(request.getStatus());

        return toResponse(judge);
    }

    @Transactional
    public void deactivateJudge(Long judgeId) {
        Judge judge = judgeRepository.findByIdAndDeletedFalse(judgeId)
            .orElseThrow(() -> new JudgeNotFoundException(judgeId));
        String suffix = "-deleted-" + System.currentTimeMillis();
        int maxBaseLength = Math.max(0, USERNAME_MAX_LENGTH - suffix.length());
        String base = judge.getUsername();
        if (maxBaseLength == 0) {
            base = Long.toString(System.currentTimeMillis());
        } else if (base.length() > maxBaseLength) {
            base = base.substring(0, maxBaseLength);
        }
        judge.setUsername((base != null ? base : "") + suffix);
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
            .judgeId(judge.getId())
            .build();
    }

    @Transactional(readOnly = true)
    public JudgeResponse getJudgeProfile(Long judgeId) {
        Judge judge = judgeRepository.findByIdAndDeletedFalse(judgeId)
            .orElseThrow(() -> new JudgeNotFoundException(judgeId));
        return toResponse(judge);
    }

    private JudgeResponse toResponse(Judge judge) {
        return JudgeResponse.builder()
            .id(judge.getId())
            .name(judge.getName())
            .username(judge.getUsername())
            .status(judge.getStatus())
            .build();
    }

}
