package com.spaceclub.notification.mail;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "mail.properties")
public record MailProperties(
        String backgroundUrl,
        String logoUrl,
        String nameEn,
        String nameKo,
        String aboutUs,
        String location,
        String phone,
        String siteUrl,
        String markdownPath
) {

}
