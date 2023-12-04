package com.spaceclub.notification.mail;

import java.util.List;

public record MailInfo(
        String[] address,
        String[] ccAddress,
        String title,
        String markdownFileName,
        String template
) {

    public static MailInfo of(
            List<String> address,
            List<String> ccAddress,
            String title,
            String markdownFileName,
            String template
    ) {
        String[] addressArray = address.toArray(new String[0]);
        String[] ccAddressArray = ccAddress.toArray(new String[0]);

        return new MailInfo(addressArray, ccAddressArray, title, markdownFileName, template);
    }
}
