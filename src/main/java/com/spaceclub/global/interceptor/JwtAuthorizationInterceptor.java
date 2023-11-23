package com.spaceclub.global.interceptor;

import com.spaceclub.global.exception.AccessTokenException;
import com.spaceclub.global.exception.TokenException;
import com.spaceclub.global.jwt.Jwt;
import com.spaceclub.global.jwt.JwtManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import static com.spaceclub.global.exception.GlobalExceptionCode.INVALID_ACCESS_TOKEN;
import static com.spaceclub.global.exception.GlobalExceptionCode.INVALID_TOKEN_FORMAT;
import static org.apache.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthorizationInterceptor implements HandlerInterceptor {

    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String REFRESH_TOKEN = "RefreshToken";

    private final JwtManager jwtManager;
    private final Jwt jwt;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String authorizationHeader = request.getHeader(AUTHORIZATION);
        validate(authorizationHeader);

        String accessToken = authorizationHeader.replace(TOKEN_PREFIX, "");
        String refreshTokenHeader = request.getHeader(REFRESH_TOKEN);

        if (refreshTokenHeader != null) {
            validate(refreshTokenHeader);
            String refreshToken = refreshTokenHeader.replace(TOKEN_PREFIX, "");

            Long userId = jwt.getClaims(accessToken).getId();
            String username = jwt.getClaims(accessToken).getUsername();

            if (jwtManager.isValidRefreshToken(refreshToken, userId)) {
                log.info("access token interceptor preHandle(), refresh token is valid");
                response.addHeader(AUTHORIZATION, jwtManager.createAccessToken(userId, username));

                return true;
            }
        }

        if (jwt.isValidFormat(accessToken)) {
            log.info("JWT interceptor preHandle(), access token이 유효합니다.");

            return true;
        }

        log.info("JWT interceptor preHandle(), 리프레시 토큰이 존재하지 않습니다.");
        throw new AccessTokenException(INVALID_ACCESS_TOKEN);
    }

    private void validate(String token) {
        boolean isWrongFormat = (token == null) || !token.startsWith(TOKEN_PREFIX);

        if (isWrongFormat) {
            throw new TokenException(INVALID_TOKEN_FORMAT);
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {
        log.info("access token interceptor postHandle()");
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        if (ex != null) {
            log.info("access token interceptor afterCompletion(), exception", ex);
            throw ex;
        }
    }

}
