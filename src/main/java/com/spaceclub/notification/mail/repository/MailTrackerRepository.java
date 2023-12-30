package com.spaceclub.notification.mail.repository;

import com.spaceclub.notification.mail.domain.MailHistory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MailTrackerRepository extends JpaRepository<MailHistory, Long> {

    int countByIsSentFalse();

    Slice<MailHistory> findAllByIsSentFalse(Pageable pageable);

}
