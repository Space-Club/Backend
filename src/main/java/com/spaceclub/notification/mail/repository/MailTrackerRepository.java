package com.spaceclub.notification.mail.repository;

import com.spaceclub.notification.mail.domain.MailTracker;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MailTrackerRepository extends JpaRepository<MailTracker, Long> {

    Page<MailTracker> findAllByIsSentFalse(Pageable pageable);

}
