package com.spaceclub.notification.mail.service;

import com.spaceclub.notification.mail.repository.MailTrackerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailRetryService {

    private final MailTrackerRepository mailTrackerRepository;
    private final MailService mailService;

    @Async
    @Transactional
    public void retryAllFailedEmails() {
        // 실패한 email count query 생성
        int sendFailedMailCount = mailTrackerRepository.countByIsSentFalse();
        final int chunkSize = 10;

        // partition size = count / chunk size 올림
        final int partitionSize = (int) Math.ceil((double) sendFailedMailCount / chunkSize);

        // partition asnyc 하게 돌리기
        for (int partitionNumber = 0; partitionNumber < partitionSize; partitionNumber++) {
            mailService.sendEmail(partitionNumber, chunkSize);
        }
    }

}
