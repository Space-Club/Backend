package com.spaceclub.global.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.servlet.HandlerInterceptor;

import static org.apache.http.HttpHeaders.AUTHORIZATION;

@RequiredArgsConstructor
public class AuthenticationInterceptor implements HandlerInterceptor {

    private final JwtAuthorizationInterceptor jwtAuthorizationInterceptor;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        if (authorizationHeader == null) {
            return true;
        }

        return jwtAuthorizationInterceptor.preHandle(request, response, handler);
    }

}
