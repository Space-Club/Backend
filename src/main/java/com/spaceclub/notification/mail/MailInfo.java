package com.spaceclub.notification.mail;

import java.util.Arrays;
import java.util.StringJoiner;

public record MailInfo(
        String[] address,
        String title,
        String template
) {

    public static MailInfo of(
            String address,
            String title,
            String template
    ){
        String[] addressArray = Arrays.stream(address.split(",")).toArray(String[]::new);

        return new MailInfo(addressArray, title, template);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", MailInfo.class.getSimpleName() + "[", "]")
                .add("address=" + Arrays.toString(address))
                .add("title='" + title + "'")
                .add("template='" + template + "'")
                .toString();
    }

}

