package com.spaceclub.notification.mail.service;

import com.spaceclub.notification.mail.MailProperties;
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

    public Context createContext(Map<String, Object> additionalInfo) {
        this.context.setVariables(additionalInfo);

        return this.context;
    }

}
