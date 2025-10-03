package com.example.trx.repository.notice;

import com.example.trx.domain.notice.Notice;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    List<Notice> findByPinnedIsTrueAndApplyAtLessThanEqualAndDeletedFalse(LocalDateTime applyAt, Sort sort);

    Page<Notice> findByPinnedIsFalseAndApplyAtLessThanEqualAndDeletedFalse(LocalDateTime applyAt, Pageable pageable);

    Optional<Notice> findByIdAndDeletedFalse(Long id);
}
