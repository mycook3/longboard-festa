package com.example.trx.repository.judge;

import com.example.trx.domain.judge.Judge;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JudgeRepository extends JpaRepository<Judge, Long> {
    boolean existsByUsername(String username);
    Optional<Judge> findByUsername(String username);
    Optional<Judge> findByIdAndDeletedFalse(Long id);
    List<Judge> findAllByDeletedFalse();
    Optional<Judge> findByUsernameAndDeletedFalse(String username);
}
