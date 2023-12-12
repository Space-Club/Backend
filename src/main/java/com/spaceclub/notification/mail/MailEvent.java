package com.spaceclub.notification.mail;

import static com.spaceclub.notification.mail.domain.Template.WELCOME;

public record MailEvent(MailInfo mailInfo) {

    public static MailEvent welcomeEvent(String email) {
        MailInfo mailInfo = MailInfo.of(
                email,
                WELCOME.getTitle(),
                WELCOME.getTemplateName()
        );

        return new MailEvent(mailInfo);
    }

}
