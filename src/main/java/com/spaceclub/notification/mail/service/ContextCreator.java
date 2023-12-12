package com.spaceclub.notification.mail.service;

import com.spaceclub.notification.mail.MailProperties;
import com.spaceclub.notification.mail.service.vo.EventStatusChangeMailInfo;
import com.spaceclub.notification.mail.service.vo.MailInfo;
import com.spaceclub.notification.mail.service.vo.WelcomeMailInfo;
import org.thymeleaf.context.Context;

import java.util.Map;

public class ContextCreator {

    private final Context context = new Context();

    public ContextCreator(MailProperties mailProperties) {
        Map<String, Object> emailValues = Map.of(
                "nameEn", mailProperties.nameEn(),
                "nameKo", mailProperties.nameKo(),
                "aboutUs", mailProperties.aboutUs(),
                "location", mailProperties.location(),
                "phone", mailProperties.phone(),
                "siteUrl", mailProperties.siteUrl()
        );
        context.setVariables(emailValues);
    }

    public Context createContext(MailInfo mailInfo) {
        if (mailInfo instanceof WelcomeMailInfo) {
            return this.context;
        }
        if (mailInfo instanceof EventStatusChangeMailInfo eventStatusChangeMailInfo) {
            addAdditionalInfo(eventStatusChangeMailInfo);
            return this.context;
        }
        throw new IllegalArgumentException("Unknown mailInfo type: " + mailInfo.getClass().getName());
    }

    private void addAdditionalInfo(EventStatusChangeMailInfo eventStatusChangeMailInfo) {
        Map<String, Object> additionalInfo = Map.of(
                "clubName", eventStatusChangeMailInfo.getClubName(),
                "eventName", eventStatusChangeMailInfo.getEventName(),
                "eventStatus", eventStatusChangeMailInfo.getEventStatus()
        );
        this.context.setVariables(additionalInfo);
    }

}
