package com.example.trx.repository.notice;

import com.example.trx.domain.notice.Notice;
import java.time.OffsetDateTime;
import java.util.List;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    List<Notice> findByApplyAtLessThanEqual(OffsetDateTime applyAt, Sort sort);
}
