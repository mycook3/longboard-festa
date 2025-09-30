package com.example.trx.repository.notice;

import com.example.trx.domain.notice.Notice;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    List<Notice> findByPinnedIsTrueAndApplyAtLessThanEqual(LocalDateTime applyAt, Sort sort);

    Page<Notice> findByPinnedIsFalseAndApplyAtLessThanEqual(LocalDateTime applyAt, Pageable pageable);
}
