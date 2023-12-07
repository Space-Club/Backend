package com.spaceclub.notification.mail;

import java.util.List;

public record MailEvent(MailInfo mailInfo) {

    public static MailEvent welcomeEvent(String email) {
        MailInfo mailInfo = MailInfo.of(
                List.of(email),
                List.of(),
                "Space Club에 가입해주셔서 감사합니다.",
                "welcome",
                "index"
        );

        return new MailEvent(mailInfo);
    }
}
