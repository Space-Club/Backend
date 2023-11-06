package com.spaceclub.global.jwt.service;

import com.spaceclub.global.jwt.Claims;
import com.spaceclub.global.jwt.Jwt;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import static com.spaceclub.user.controller.UserController.AUTHORIZATION_HEADER;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final Jwt jwt;

    public String createToken(Long userId, String username) {
        return jwt.sign(Claims.from(userId, username));
    }

    public Long verifyUserId(HttpServletRequest request) {
        String header = request.getHeader(AUTHORIZATION_HEADER);

        if (header == null || !header.startsWith("Bearer ")) {
            throw new IllegalArgumentException("토큰이 없거나 형식이 유효하지 않습니다.");
        }
        String token = header.split(" ")[1];

        return jwt.verify(token).getId();
    }

}
