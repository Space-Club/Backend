package com.spaceclub.global.config;

import com.spaceclub.global.UserArgumentResolver;
import com.spaceclub.global.interceptor.AuthenticationInterceptor;
import com.spaceclub.global.interceptor.JwtAuthorizationInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Profile(value = {"develop", "local"})
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final JwtAuthorizationInterceptor jwtAccessTokenInterceptor;
    private final UserArgumentResolver userArgumentResolver;

    @Profile("develop")
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173", "https://spaceclub.vercel.app", "https://spaceclub.site")
                .allowedMethods("OPTIONS", "GET", "POST", "PATCH", "DELETE")
                .allowCredentials(true)
                .exposedHeaders("Location")
                .maxAge(1800); // 1800초, 30분으로 설정
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthenticationInterceptor(jwtAccessTokenInterceptor))
                .addPathPatterns(
                        "/api/v1/events/{eventId}",
                        "/api/v1/users*",
                        "/api/v1/clubs/invite/{code}",
                        "/api/v1/events**",
                        "/api/v1/users*",
                        "/api/v1/users/oauths",
                        "/api/v1/clubs/invite/{code}"
                        ) // 인가
                .order(1);

        registry.addInterceptor(jwtAccessTokenInterceptor)
                .order(2)
                .addPathPatterns("/api/v1/**")
                .excludePathPatterns(
                        "/api/v1/events/{eventId}",
                        "/api/v1/users*",
                        "/api/v1/clubs/invite/{code}",
                        "/api/v1/events**",
                        "/api/v1/users*",
                        "/api/v1/users/oauths",
                        "/api/v1/clubs/invite/{code}"
                );
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(userArgumentResolver);
    }

}
