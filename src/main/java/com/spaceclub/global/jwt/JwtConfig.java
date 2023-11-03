package com.spaceclub.global.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(value = JwtProperties.class)
public class JwtConfig {

    private final JwtProperties jwtProperties;

    @Bean
    public Jwt jwt() {
        return Jwt.builder()
                .issuer(jwtProperties.getIssuer())
                .clientSecret(jwtProperties.getClientSecret())
                .expirySeconds(jwtProperties.getExpirySeconds())
                .build();
    }

}
