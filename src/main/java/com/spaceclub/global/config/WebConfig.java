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

    private final JwtAuthorizationInterceptor jwtAuthorizationInterceptor;
    private final UserArgumentResolver userArgumentResolver;

    @Profile("develop")
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173", "https://spaceclub.vercel.app", "https://spaceclub.site")
                .allowedMethods("OPTIONS", "GET", "POST", "PATCH", "DELETE")
                .allowCredentials(true)
                .exposedHeaders("Location")
                .maxAge(1800);
    }

    /**
     * 인증 및 인가
     * 인가 처리가 optional or 필요 없는 endpoint는 인증만 처리 (AuthenticationInterceptor)
     * 인가 처리가 필수 적인 endpoint는 인가 처리 (JwtAuthorizationInterceptor)
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new AuthenticationInterceptor(jwtAuthorizationInterceptor))
                .addPathPatterns(
                        "/api/v1/users/oauth**",
                        "/api/v1/users*",
                        "/api/v1/clubs/invite**",
                        "/api/v1/events**",
                        "/api/v1/events/**"
                        )
                .order(1);

        registry.addInterceptor(jwtAuthorizationInterceptor)
                .order(2)
                .addPathPatterns("/api/v1/**")
                .excludePathPatterns(
                        "/api/v1/users/oauth**",
                        "/api/v1/users*",
                        "/api/v1/clubs/invite**",
                        "/api/v1/events**",
                        "/api/v1/events/**"
                );
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(userArgumentResolver);
    }

}
