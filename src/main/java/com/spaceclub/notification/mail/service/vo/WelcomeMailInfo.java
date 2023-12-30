package com.spaceclub.notification.mail.service.vo;

import com.spaceclub.notification.mail.domain.MailTracker;
import com.spaceclub.notification.mail.domain.TemplateName;

import java.time.LocalDateTime;

import static com.spaceclub.notification.mail.domain.TemplateName.WELCOME;

public class WelcomeMailInfo extends MailInfo {

    private static final String DELIMITER = ",";

    private WelcomeMailInfo(String[] addresses) {
        super(addresses);
    }

    public static WelcomeMailInfo from(String email) {
        String[] addressArray = email.split(DELIMITER);

        return new WelcomeMailInfo(addressArray);
    }

    public static MailTracker createMailHistory(MailInfo mailInfo, boolean isSent) {
        return MailTracker.builder()
                .addresses(String.join(DELIMITER, mailInfo.email()))
                .title(mailInfo.title())
                .templateName(TemplateName.findByTemplateName(mailInfo.templateName()))
                .sentAt(LocalDateTime.now())
                .isSent(isSent)
                .build();
    }

    @Override
    public String title() {
        return WELCOME.getTitle();
    }

    @Override
    public String templateName() {
        return WELCOME.getTemplateName();
    }

}

