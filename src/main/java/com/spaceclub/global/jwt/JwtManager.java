package com.spaceclub.global.jwt;

import com.spaceclub.global.jwt.Claims;
import com.spaceclub.global.jwt.Jwt;
import com.spaceclub.user.domain.User;
import com.spaceclub.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.spaceclub.global.ExceptionCode.USER_NOT_FOUND;

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
        jwt.verify(refreshToken);

        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(USER_NOT_FOUND.toString()))
                .getRefreshToken().equals(refreshToken);
    }

    public Claims getClaims(String accessToken) {
        return jwt.getClaims(accessToken);
    }

}

