package com.spaceclub.global.jwt.vo;

import com.spaceclub.global.jwt.Claims;

public record JwtUser(Long id, String username) {

    public static JwtUser from(Claims claims) {
        Long userId = claims.getId();
        String username = claims.getUsername();

        return new JwtUser(userId, username);
    }

}
