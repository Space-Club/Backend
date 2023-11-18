package com.spaceclub.global.jwt.service;

import com.spaceclub.global.jwt.Claims;
import com.spaceclub.global.jwt.Jwt;
import com.spaceclub.user.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Component
@RequiredArgsConstructor
public class JwtManager {

    private final Jwt jwt;
    private final UserRepository userRepository;

    public String createAccessToken(Long userId, String username) {
        return jwt.signAccessToken(Claims.from(userId, username));
    }

    public String createRefreshToken() {
        return jwt.signRefreshToken();
    }


    public Long verifyUserId(HttpServletRequest request) {
        String header = request.getHeader(AUTHORIZATION);

        Assert.notNull(header, "토큰이 필수입니다.");
        Assert.isTrue(header.startsWith("Bearer "), "토큰이 유효하지 않은 형식입니다.");

        String token = header.split(" ")[1];

        return getClaims(token).getId();
    }


    public boolean isValidRefreshToken(String refreshToken, Long userId) {
        jwt.verify(refreshToken);

        return userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."))
                .getRefreshToken().equals(refreshToken);
    }

    public Claims getClaims(String accessToken) {
        return jwt.getClaims(accessToken);
    }

}

