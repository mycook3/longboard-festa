package com.example.trx.repository.notice;

import com.example.trx.domain.notice.Notice;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    List<Notice> findByPinnedIsTrueAndApplyAtLessThanEqual(OffsetDateTime applyAt, Sort sort);

    Page<Notice> findByPinnedIsFalseAndApplyAtLessThanEqual(OffsetDateTime applyAt, Pageable pageable);
}
