package com.spaceclub.global.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Profile("develop")
@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("https://spaceclub.site/api/v1/**") // 모든 도메인에 대해 허용
                .allowedMethods("*")
                .allowCredentials(true)
                .maxAge(1800); // 1800초, 30분으로 설정
    }

}
