package com.example.trx.service.judge;

import com.example.trx.apis.judge.dto.JudgeCreateRequest;
import com.example.trx.apis.judge.dto.JudgeResponse;
import com.example.trx.apis.judge.dto.JudgeUpdateRequest;
import com.example.trx.domain.judge.Judge;
import com.example.trx.domain.judge.JudgeStatus;
import com.example.trx.domain.judge.exception.JudgeAlreadyExistsException;
import com.example.trx.domain.judge.exception.JudgeNotFoundException;
import com.example.trx.repository.judge.JudgeRepository;
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

    @Transactional
    public JudgeResponse createJudge(JudgeCreateRequest request) {
        if (judgeRepository.existsByUsername(request.getUsername())) {
            throw new JudgeAlreadyExistsException(request.getUsername());
        }

        Judge judge = Judge.builder()
            .name(request.getName())
            .username(request.getUsername())
            .password(passwordEncoder.encode(request.getPassword()))
            .disciplineCode(request.getDisciplineCode())
            .judgeNumber(request.getJudgeNumber())
            .status(JudgeStatus.ACTIVE)
            .build();

        Judge saved = judgeRepository.save(judge);

        return JudgeResponse.builder()
            .id(saved.getId())
            .name(saved.getName())
            .username(saved.getUsername())
            .judgeNumber(saved.getJudgeNumber())
            .disciplineCode(saved.getDisciplineCode())
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
                .disciplineCode(judge.getDisciplineCode())
                .status(judge.getStatus())
                .build())
            .collect(Collectors.toList());
    }

    @Transactional
    public JudgeResponse updateJudge(Long judgeId, JudgeUpdateRequest request) {
        Judge judge = judgeRepository.findByIdAndDeletedFalse(judgeId)
            .orElseThrow(() -> new JudgeNotFoundException(judgeId));

        judge.setName(request.getName());
        judge.setDisciplineCode(request.getDisciplineCode());
        judge.setStatus(request.getStatus());

        return JudgeResponse.builder()
            .id(judge.getId())
            .name(judge.getName())
            .username(judge.getUsername())
            .judgeNumber(judge.getJudgeNumber())
            .disciplineCode(judge.getDisciplineCode())
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
}
