package com.spaceclub.global.mail;

import com.spaceclub.global.mail.domain.MailTracker;
import com.spaceclub.global.mail.repository.MailTrackerRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.event.TransactionalEventListener;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.time.LocalDateTime;
import java.util.HashMap;

import static com.fasterxml.jackson.core.JsonEncoding.UTF8;
import static com.spaceclub.global.mail.HtmlConverter.markdownToHtml;
import static org.springframework.transaction.annotation.Propagation.REQUIRES_NEW;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

    private static final String MARKDOWN_FILE_EXTENSION = ".md";

    private static final String DELIMITER = ",";

    private final JavaMailSender emailSender;

    private final SpringTemplateEngine templateEngine;

    private final MailProperties mailProperties;

    private final MailTrackerRepository mailTrackerRepository;

    @Async
    @Transactional(propagation = REQUIRES_NEW)
    @TransactionalEventListener
    public void sendEmail(MailEvent mailEvent) {
        MimeMessage message = emailSender.createMimeMessage();
        MailInfo mailInfo = mailEvent.mailInfo();

        Context context = createContext(mailInfo.markdownFileName());
        String html = templateEngine.process(mailInfo.template(), context);
        boolean isSent = true;

        try{
            MimeMessageHelper helper = new MimeMessageHelper(message, true, UTF8.getJavaName());
            helper.setSubject(mailInfo.title());
            helper.setTo(mailInfo.address());
            helper.setCc(mailInfo.ccAddress());
            helper.setTo(mailInfo.address());
            helper.setText(html, true);
            helper.addInline("image1", new ClassPathResource(mailProperties.backgroundUrl()));
            helper.addInline("image2", new ClassPathResource(mailProperties.logoUrl()));

            emailSender.send(message);
        } catch (MessagingException e) {
            log.error("메일 전송 실패", e);
            isSent = false;

        } finally {
            String addresses = String.join(DELIMITER, mailInfo.address());
            String ccAddresses = String.join(DELIMITER, mailInfo.ccAddress());

            MailTracker mailHistory = MailTracker.builder()
                    .addresses(addresses)
                    .ccAddresses(ccAddresses)
                    .title(mailInfo.title())
                    .markdownFileName(mailInfo.markdownFileName())
                    .template(mailInfo.template())
                    .sentAt(LocalDateTime.now())
                    .isSent(isSent)
                    .build();

            mailTrackerRepository.save(mailHistory);
        }
        log.info("메일 발송 완료!");
    }

    private Context createContext(String markdownFileName) {
        Context context = new Context();
        HashMap<String, String> emailValues = new HashMap<>();
        emailValues.put("nameEn", mailProperties.nameEn());
        emailValues.put("nameKo", mailProperties.nameKo());
        emailValues.put("aboutUs", mailProperties.aboutUs());
        emailValues.put("content", markdownToHtml(mailProperties.markdownPath()).concat(markdownFileName).concat(MARKDOWN_FILE_EXTENSION));
        emailValues.put("location", mailProperties.location());
        emailValues.put("phone", mailProperties.phone());
        emailValues.put("siteUrl", mailProperties.siteUrl());
        emailValues.forEach(context::setVariable);

        return context;
    }

}
