package com.spaceclub.global.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jwt")
public record JwtProperties(
        String header,
        String issuer,
        String clientSecret,
        int expirySeconds,
        int refreshExpirySeconds
) {
}

