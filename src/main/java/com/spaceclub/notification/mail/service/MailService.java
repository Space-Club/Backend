package com.spaceclub.notification.mail.service;

import com.spaceclub.notification.mail.MailProperties;
import com.spaceclub.notification.mail.domain.MailTracker;
import com.spaceclub.notification.mail.repository.MailTrackerRepository;
import com.spaceclub.notification.mail.service.event.MailEvent;
import com.spaceclub.notification.mail.service.vo.MailInfo;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.LocalDateTime;

import static com.fasterxml.jackson.core.JsonEncoding.UTF8;
import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

    private static final String DELIMITER = ",";

    private final JavaMailSender emailSender;

    private final SpringTemplateEngine templateEngine;

    private final MailProperties mailProperties;

    private final MailTrackerRepository mailTrackerRepository;

    @Async
    @Transactional(propagation = REQUIRES_NEW)
    @TransactionalEventListener
    public void sendEmail(MailEvent mailEvent) {
        MailInfo mailInfo = mailEvent.mailInfo();
        Context context = new ContextCreator(mailProperties).createContext(mailInfo);

        String html = templateEngine.process(mailInfo.templateName(), context);

        boolean isSent = true;
        try {
            MimeMessage message = createMailMessage(mailInfo, html);
            emailSender.send(message);
        } catch (MessagingException e) {
            log.error("메일 전송 실패", e);
            isSent = false;
        } finally {
            String addresses = String.join(DELIMITER, mailInfo.email());
            MailTracker mailHistory = MailTracker.builder()
                    .addresses(addresses)
                    .title(mailInfo.title())
                    .template(mailInfo.templateName())
                    .sentAt(LocalDateTime.now())
                    .isSent(isSent)
                    .build();

            mailTrackerRepository.save(mailHistory);
        }
        log.debug("메일 발송 완료!");
    }

    @Async
    @Transactional(propagation = REQUIRES_NEW)
    public void sendEmail(int partitionNumber, int chunkSize) {
        Pageable pageRequest = PageRequest.of(partitionNumber, chunkSize);
        Slice<MailTracker> mailTrackers = mailTrackerRepository.findAllByIsSentFalse(pageRequest);

        mailTrackers.forEach(mailTracker -> {
            // TODO: html을 db에 저장하는 형식으로 리팩토링 예정
//            MailInfo.of(mailTracker.getAddresses(), mailTracker.getTemplate());
//            WelcomeMailInfo welcomeMailInfo = WelcomeMailInfo.of(mailTracker.getAddresses(), mailTracker.getTitle(), mailTracker.getTemplate());
//            log.debug("mailInfo: {}", welcomeMailInfo);
//            sendEachMail(welcomeMailInfo, mailTracker.getId());
        });
    }

    public void sendEachMail(MailInfo mailInfo, Long mailTrackerId) {
        Context context = new ContextCreator(mailProperties).createContext(mailInfo);
        String html = templateEngine.process(mailInfo.templateName(), context);
        try {
            MimeMessage message = createMailMessage(mailInfo, html);
            emailSender.send(message);
        } catch (MessagingException | MailException e) {
            log.error("메일 전송 실패", e);
            return;
        }

        mailTrackerRepository.findById(mailTrackerId)
                .ifPresent(MailTracker::changeToSent);

        log.debug("메일 재전송 완료");
    }

    private MimeMessage createMailMessage(MailInfo mailInfo, String html) throws MessagingException {
        MimeMessage message = emailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, UTF8.getJavaName());
        helper.setSubject(mailInfo.title());
        helper.setTo(mailInfo.email());
        helper.setText(html, true);
        helper.addInline("image1", new ClassPathResource(mailProperties.backgroundUrl()));
        helper.addInline("image2", new ClassPathResource(mailProperties.logoUrl()));

        return message;
    }

}
