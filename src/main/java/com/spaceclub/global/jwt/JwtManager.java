package com.spaceclub.global.jwt;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.spaceclub.global.exception.RefreshTokenException;
import com.spaceclub.user.domain.User;
import com.spaceclub.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.spaceclub.global.exception.GlobalExceptionCode.INVALID_REFRESH_TOKEN;
import static com.spaceclub.user.UserExceptionMessage.USER_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class JwtManager {

    private final Jwt jwt;
    private final UserRepository userRepository;

    public String createAccessToken(Long userId, String username) {
        return jwt.signAccessToken(Claims.from(userId, username));
    }

    @Transactional
    public String createRefreshToken(Long userId) {
        String refreshToken = jwt.signRefreshToken();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(USER_NOT_FOUND.toString()))
                .updateRefreshToken(refreshToken);
        userRepository.save(user);

        return refreshToken;
    }

    public boolean isValidRefreshToken(String refreshToken, Long userId) {
        verifyRefreshToken(refreshToken);

        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(USER_NOT_FOUND.toString()))
                .getRefreshToken().equals(refreshToken);
    }

    private void verifyRefreshToken(String refreshToken) {
        try {
            jwt.verify(refreshToken);
        } catch (JWTVerificationException e) {
            throw new RefreshTokenException(INVALID_REFRESH_TOKEN);
        }
    }

    public Claims getClaims(String accessToken) {
        return jwt.getClaims(accessToken);
    }

}

