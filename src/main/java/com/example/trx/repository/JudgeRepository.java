package com.example.trx.repository;

import com.example.trx.domain.judge.Judge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JudgeRepository extends JpaRepository<Judge, Long> {

}
