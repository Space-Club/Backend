package com.spaceclub.notification.mail.service.event;

import com.spaceclub.notification.mail.service.vo.EventStatusChangeMailInfo;
import com.spaceclub.notification.mail.service.vo.MailInfo;
import com.spaceclub.notification.mail.service.vo.WelcomeMailInfo;

public record MailEvent(MailInfo mailInfo) {

    public static MailEvent createMailEvent(String email) {
        WelcomeMailInfo welcomeMailInfo = WelcomeMailInfo.from(email);

        return new MailEvent(welcomeMailInfo);
    }

    public static MailEvent createMailEvent(String email, String clubName, String eventName, String eventStatus) {
        EventStatusChangeMailInfo mailInfo = EventStatusChangeMailInfo.from(email, clubName, eventName, eventStatus);

        return new MailEvent(mailInfo);
    }

}
