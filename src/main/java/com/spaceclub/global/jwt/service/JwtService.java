package com.spaceclub.global.jwt.service;

import com.spaceclub.global.jwt.Claims;
import com.spaceclub.global.jwt.Jwt;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final Jwt jwt;

    public String createToken(Long userId, String username) {
        return jwt.sign(Claims.from(userId, username));
    }

    public Long verifyUserId(HttpServletRequest request) {
        String header = request.getHeader(AUTHORIZATION);

        Assert.notNull(header, "토큰이 필수입니다.");
        Assert.isTrue(header.startsWith("Bearer "), "토큰이 유효하지 않은 형식입니다.");

        String token = header.split(" ")[1];

        return jwt.verify(token).getId();
    }

}
