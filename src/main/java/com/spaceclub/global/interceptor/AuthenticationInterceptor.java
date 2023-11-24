package com.spaceclub.global.interceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import static org.apache.http.HttpHeaders.AUTHORIZATION;

@Component
@RequiredArgsConstructor
public class AuthenticationInterceptor implements HandlerInterceptor {

    private static final String REFRESH_TOKEN = "RefreshToken";

    private final JwtAuthenticationProvider jwtAuthenticationProvider;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        String refreshTokenHeader = request.getHeader(REFRESH_TOKEN);

        boolean isTokenDoesNotExists = authorizationHeader == null && refreshTokenHeader == null;
        if (isTokenDoesNotExists) {
            return true;
        }

        return jwtAuthenticationProvider.authenticate(authorizationHeader, refreshTokenHeader, response);
    }

}
