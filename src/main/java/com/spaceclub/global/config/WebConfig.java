package com.spaceclub.global.config;

import com.spaceclub.global.UserArgumentResolver;
import com.spaceclub.global.interceptor.AuthenticationInterceptor;
import com.spaceclub.global.interceptor.AuthorizationInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Profile({"develop", "local"})
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    private final UserArgumentResolver userArgumentResolver;
    private final AuthenticationInterceptor authenticationInterceptor;
    private final AuthorizationInterceptor authorizationInterceptor;

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

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authenticationInterceptor)
                .addPathPatterns("/api/v1/**")
                .order(1);

        registry.addInterceptor(authorizationInterceptor)
                .order(2)
                .addPathPatterns("/api/v1/**")
                .excludePathPatterns( // 반드시 인가 처리가 필수가 아닌 path 추가
                        "/api/v1/users/oauth**"
                );
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(userArgumentResolver);
    }

}
