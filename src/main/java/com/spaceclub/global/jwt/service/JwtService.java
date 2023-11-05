package com.spaceclub.global.jwt.service;

import com.spaceclub.global.jwt.Claims;
import com.spaceclub.global.jwt.Jwt;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtService {

    private final Jwt jwt;

    public String createToken(Long userId, String username) {
        return jwt.sign(Claims.from(userId, username));
    }

}
