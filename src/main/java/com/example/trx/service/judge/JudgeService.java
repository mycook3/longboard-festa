package com.example.trx.service.judge;

import com.example.trx.apis.judge.dto.JudgeCreateRequest;
import com.example.trx.apis.judge.dto.JudgeResponse;
import com.example.trx.domain.judge.Judge;
import com.example.trx.domain.judge.JudgeStatus;
import com.example.trx.domain.judge.exception.JudgeAlreadyExistsException;
import com.example.trx.repository.judge.JudgeRepository;
import java.util.Optional;
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
}
