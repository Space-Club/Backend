package com.spaceclub.global.config.s3.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "s3.iam")
public record S3IamProperties(
        String accessKey,
        String secretKey,
        String region
) {

}
