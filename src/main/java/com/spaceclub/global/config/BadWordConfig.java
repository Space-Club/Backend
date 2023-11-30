package com.spaceclub.global.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "bad-word")
public record BadWordConfig(
        String path
) {

}
