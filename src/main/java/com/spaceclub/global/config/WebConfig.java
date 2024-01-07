package com.spaceclub.global.config;

import com.spaceclub.global.UserArgumentResolver;
import com.spaceclub.global.interceptor.AuthenticationInterceptor;
import com.spaceclub.global.interceptor.AuthorizationInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@EnableScheduling
@RequiredArgsConstructor
@ConfigurationPropertiesScan(basePackages = {"com.spaceclub.global.config"})
public class WebConfig implements WebMvcConfigurer {

    private final UserArgumentResolver userArgumentResolver;

    private final AuthenticationInterceptor authenticationInterceptor;

    private final AuthorizationInterceptor authorizationInterceptor;

    private final WebProperties webProperties;

    @Profile("develop")
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(webProperties.allowedOrigins().toArray(String[]::new))
                .allowedMethods("*")
                .allowCredentials(true)
                .exposedHeaders("Location", "RefreshToken")
                .maxAge(1800);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authenticationInterceptor)
                .addPathPatterns(webProperties.interceptorPathPattern())
                .order(1);

        registry.addInterceptor(authorizationInterceptor)
                .addPathPatterns(webProperties.interceptorPathPattern())
                .order(2);
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(userArgumentResolver);
    }

}

