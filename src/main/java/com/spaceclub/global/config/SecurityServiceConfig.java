package com.spaceclub.global.config;

import com.spaceclub.global.filter.JwtAuthorizationFilter;
import com.spaceclub.global.jwt.JwtProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class SecurityServiceConfig {

    private final JwtProperties jwtProperties;

    @Bean
    public JwtAuthorizationFilter jwtAuthenticationFilter() {
        return new JwtAuthorizationFilter(jwtProperties);
    }

}
