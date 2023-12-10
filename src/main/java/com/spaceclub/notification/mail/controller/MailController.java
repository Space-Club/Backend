package com.spaceclub.notification.mail.controller;

import com.spaceclub.notification.mail.service.MailRetryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/mails")
@RequiredArgsConstructor
public class MailController {

    private final MailRetryService mailRetryService;

    @PostMapping("/retry-all-failed-emails")
    public void retryAllFailedEmails() {
        mailRetryService.retryAllFailedEmails();
    }

}
