package com.spaceclub.notification.mail.service;

import com.spaceclub.notification.mail.MailProperties;
import com.spaceclub.notification.mail.domain.MailTracker;
import com.spaceclub.notification.mail.domain.TemplateName;
import com.spaceclub.notification.mail.repository.MailTrackerRepository;
import com.spaceclub.notification.mail.repository.TemplateRepository;
import com.spaceclub.notification.mail.service.event.MailEvent;
import com.spaceclub.notification.mail.service.vo.EventStatusChangeMailInfo;
import com.spaceclub.notification.mail.service.vo.MailInfo;
import com.spaceclub.notification.mail.service.vo.WelcomeMailInfo;
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

import java.util.Collections;
import java.util.Map;

import static com.fasterxml.jackson.core.JsonEncoding.UTF8;
import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

    private final JavaMailSender emailSender;

    private final SpringTemplateEngine templateEngine;

    private final MailProperties mailProperties;

    private final MailTrackerRepository mailTrackerRepository;

    private final TemplateRepository templateRepository;

    @Async
    @Transactional(propagation = REQUIRES_NEW)
    @TransactionalEventListener
    public void sendEmail(MailEvent mailEvent) {
        MailInfo mailInfo = mailEvent.mailInfo();

        TemplateName templateName = TemplateName.findByTemplateName(mailInfo.templateName());
        String templateHtml = templateRepository.findTemplateByTemplateName(templateName);

        Map<String, Object> additionalInfo = getAdditionalInfo(templateName, mailInfo);
        Context context = new ContextCreator(mailProperties).createContext(additionalInfo);

        String html = templateEngine.process(templateHtml, context);

        boolean isSent = true;
        try {
            MimeMessage message = createMailMessage(mailInfo, html);
            emailSender.send(message);
        } catch (MessagingException e) {
            log.error("메일 전송 실패", e);
            isSent = false;
        } finally {
            MailTracker mailHistory = switch (templateName) {
                case WELCOME -> WelcomeMailInfo.createMailHistory(mailInfo, isSent);
                case EVENT_STATUS_CHANGED -> EventStatusChangeMailInfo.from(mailInfo, isSent);
            };

            mailTrackerRepository.save(mailHistory);
        }
        log.debug("메일 발송 완료!");
    }

    @Async
    @Transactional(propagation = REQUIRES_NEW)
    public void sendEmail(int partitionNumber, int chunkSize) {
        Pageable pageRequest = PageRequest.of(partitionNumber, chunkSize);
        Slice<MailTracker> mailTrackers = mailTrackerRepository.findAllByIsSentFalse(pageRequest);

        mailTrackers.forEach(this::sendEachMail);
    }

    private void sendEachMail(MailTracker mailTracker) {
        MailInfo mailInfo = getInfo(mailTracker);
        Map<String, Object> additionalInfo = getAdditionalInfo(mailTracker.getTemplateName(), mailInfo);
        Context context = new ContextCreator(mailProperties).createContext(additionalInfo);

        String templateHtml = templateRepository.findById(mailTracker.getTemplateId()).orElseThrow().getTemplate();
        String html = templateEngine.process(templateHtml, context);

        try {
            MimeMessage message = createMailMessage(mailInfo, html);
            emailSender.send(message);
        } catch (MessagingException | MailException e) {
            log.error("메일 전송 실패", e);
            return;
        }
        mailTracker.changeToSent();
        log.debug("메일 재전송 완료");
    }

    private Map<String, Object> getAdditionalInfo(TemplateName templateName, MailInfo mailInfo) {
        return switch (templateName) {
            case WELCOME -> Collections.emptyMap();
            case EVENT_STATUS_CHANGED -> {
                EventStatusChangeMailInfo eventStatusChangeMailInfo = (EventStatusChangeMailInfo) mailInfo;
                yield Map.of(
                        "clubName", eventStatusChangeMailInfo.getClubName(),
                        "eventName", eventStatusChangeMailInfo.getEventName(),
                        "eventStatus", eventStatusChangeMailInfo.getEventStatus()
                );
            }
        };
    }

    private MailInfo getInfo(MailTracker mailTracker) {
        return switch (mailTracker.getTemplateName()) {
            case WELCOME -> WelcomeMailInfo.from(mailTracker.getAddresses());
            case EVENT_STATUS_CHANGED -> EventStatusChangeMailInfo.from(mailTracker);
        };
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
