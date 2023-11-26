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

@Profile({"develop", "local"})
@Configuration
@EnableScheduling
@RequiredArgsConstructor
@ConfigurationPropertiesScan(basePackages = {"com.spaceclub.global.config"})
public class WebConfig implements WebMvcConfigurer {

    private final UserArgumentResolver userArgumentResolver;
    private final AuthenticationInterceptor authenticationInterceptor;
    private final AuthorizationInterceptor authorizationInterceptor;

    @Profile("develop")
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173", "https://spaceclub.vercel.app", "https://spaceclub.site")
                .allowedMethods("*")
                .allowCredentials(true)
                .exposedHeaders("Location")
                .maxAge(1800);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authenticationInterceptor)
                .addPathPatterns("/api/v1/**")
                .order(1);

        registry.addInterceptor(authorizationInterceptor)
                .addPathPatterns("/api/v1/**")
                .order(2);
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(userArgumentResolver);
    }

}

