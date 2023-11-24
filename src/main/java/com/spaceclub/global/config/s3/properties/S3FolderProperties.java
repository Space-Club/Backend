package com.spaceclub.global.config.s3.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "s3.folder")
public record S3FolderProperties(
        String eventPoster,
        String clubLogo,
        String clubCover,
        String userProfileImage
) {

}
