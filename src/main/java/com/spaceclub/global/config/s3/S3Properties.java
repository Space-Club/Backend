package com.spaceclub.global.config.s3;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "s3")
public record S3Properties(
        String bucket,
        Iam iam,
        String url,
        List<String> validExtensions
) {

    public record Iam(
            String accessKey,
            String secretKey,
            String region
    ) {

    }

}
