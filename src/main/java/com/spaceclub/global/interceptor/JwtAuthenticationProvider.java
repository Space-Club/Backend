package com.spaceclub.global.interceptor;

import com.spaceclub.global.exception.AccessTokenException;
import com.spaceclub.global.exception.RefreshTokenException;
import com.spaceclub.global.exception.TokenException;
import com.spaceclub.global.jwt.Jwt;
import com.spaceclub.global.jwt.JwtManager;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import static com.spaceclub.global.exception.GlobalExceptionCode.INVALID_ACCESS_TOKEN;
import static com.spaceclub.global.exception.GlobalExceptionCode.INVALID_REFRESH_TOKEN;
import static com.spaceclub.global.exception.GlobalExceptionCode.INVALID_TOKEN_FORMAT;
import static org.apache.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationProvider {

    private static final String TOKEN_PREFIX = "Bearer ";

    private final JwtManager jwtManager;
    private final Jwt jwt;

    public boolean authenticate(String authorizationHeader, String refreshTokenHeader, HttpServletResponse response) {
        validate(authorizationHeader);

        String accessToken = authorizationHeader.replace(TOKEN_PREFIX, "");

        if (refreshTokenHeader != null) {
            validate(refreshTokenHeader);
            String refreshToken = refreshTokenHeader.replace(TOKEN_PREFIX, "");

            Long userId = jwt.getClaims(accessToken).getId();
            String username = jwt.getClaims(accessToken).getUsername();

            if (jwtManager.isValidRefreshToken(refreshToken, userId)) {
                log.info("리프레시 토큰이 유효합니다. 새로운 access token을 발급합니다.");
                response.addHeader(AUTHORIZATION, jwtManager.createAccessToken(userId, username));

                return true;
            }
            throw new RefreshTokenException(INVALID_REFRESH_TOKEN);
        }

        if (jwt.isValidFormat(accessToken)) {
            log.info("access token이 유효해 인증에 성공했습니다.");

            return true;
        }
        throw new AccessTokenException(INVALID_ACCESS_TOKEN);
    }

    private void validate(String token) {
        boolean isWrongFormat = (token == null) || !token.startsWith(TOKEN_PREFIX);

        if (isWrongFormat) {
            throw new TokenException(INVALID_TOKEN_FORMAT);
        }
    }

}
