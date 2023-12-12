package com.spaceclub.notification.mail.service.vo;

import java.util.Arrays;
import java.util.StringJoiner;

public abstract class MailInfo {

    private final String[] addresses;

    public MailInfo(String[] addresses) {
        this.addresses = addresses;
    }

    public String[] email() {
        return this.addresses;
    }

    public abstract String title();

    public abstract String templateName();

    @Override
    public String toString() {
        return new StringJoiner(", ", MailInfo.class.getSimpleName() + "[", "]")
                .add("addresses=" + Arrays.toString(addresses))
                .toString();
    }

}
