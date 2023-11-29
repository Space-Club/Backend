package com.spaceclub.global.mail;

public record MailInfo(
        String[] address,
        String[] ccAddress,
        String title,
        String markdownFileName,
        String template
) {

}
