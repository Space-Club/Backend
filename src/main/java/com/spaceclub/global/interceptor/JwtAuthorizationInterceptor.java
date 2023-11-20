package com.spaceclub.global.interceptor;

import com.spaceclub.global.jwt.Jwt;
import com.spaceclub.global.jwt.JwtManager;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

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
        if (jwt.isValidFormat(accessToken)) { // 올바른 토큰 아니면 예외 발생 exception handler에서 catch 예정, 만료 시간
            //JWTDecodeException (dispatcher servlet에서 발생) // 예외 처리 후 제거 예정
            log.info("access token interceptor preHandle(), access token is valid");

            return true;
        }
        log.info("access token interceptor preHandle(), access token is expired");

        Long userId = jwt.getClaims(accessToken).getId();
        String username = jwt.getClaims(accessToken).getUsername();

        if (isRefreshTokenExists(request)) { // 만료되고 refresh token값이 있으면
            String refreshTokenHeader = request.getHeader(REFRESH_TOKEN);
            validate(refreshTokenHeader);

            String refreshToken = refreshTokenHeader.replace(TOKEN_PREFIX, "");

            if (jwtManager.isValidRefreshToken(refreshToken, userId)) { // refresh token verify ->
                log.info("access token interceptor preHandle(), refresh token is valid");
                response.addHeader(AUTHORIZATION, jwtManager.createAccessToken(userId, username));

                return true;
            }

            log.info("access token interceptor preHandle(), refresh token is invalid, redirect to login page"); // 예외 던지기
            throw new IllegalArgumentException("access token interceptor preHandle(), refresh token is invalid");
        }
        log.info("access token interceptor preHandle(), refresh token does not exist");
        throw new IllegalArgumentException("access token interceptor preHandle(), refresh token does not exist");
    }

    private boolean isRefreshTokenExists(HttpServletRequest request) {
        return request.getHeader(REFRESH_TOKEN) != null;
    }

    private void validate(String token) {
        Assert.notNull(token, "토큰이 필수입니다.");
        Assert.isTrue(token.startsWith(TOKEN_PREFIX), "토큰이 유효하지 않은 형식입니다.");
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
