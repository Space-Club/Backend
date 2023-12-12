package com.spaceclub.notification.mail.service.vo;

import static com.spaceclub.notification.mail.domain.Template.WELCOME;

public class WelcomeMailInfo extends MailInfo {

    private static final String DELIMITER = ",";

    private WelcomeMailInfo(String[] addresses) {
        super(addresses);
    }

    public static WelcomeMailInfo from(String email) {
        String[] addressArray = email.split(DELIMITER);

        return new WelcomeMailInfo(addressArray);
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

