package com.spaceclub.global.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.HashMap;

import static com.fasterxml.jackson.core.JsonEncoding.UTF8;
import static com.spaceclub.global.mail.HtmlConverter.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MailService {

    private static final String MARKDOWN_FILE_EXTENSION = ".md";

    private final JavaMailSender emailSender;
    private final SpringTemplateEngine templateEngine;
    private final MailProperties mailProperties;

    @Async
    public void sendEmail(MailInfo mailInfo) {
        MimeMessage message = emailSender.createMimeMessage();
        Context context = createContext(mailInfo.markdownFileName());
        String html = templateEngine.process(mailInfo.template(), context);

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
            // db 쌓기
            throw new RuntimeException(e);
        }
        log.info("메일 발송 완료!");
        // db 쌓기
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
