package com.spaceclub.global.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.TokenExpiredException;
import lombok.Builder;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;

@Slf4j
public class Jwt {

    private final String issuer;

    private final int expirySeconds;

    private final int refreshTokenExpirySeconds;

    private final Algorithm algorithm;

    private final JWTVerifier jwtVerifier;

    @Builder
    private Jwt(
            String issuer,
            String clientSecret,
            int expirySeconds,
            int refreshTokenExpirySeconds) {
        this.issuer = issuer;
        this.expirySeconds = expirySeconds;
        this.algorithm = Algorithm.HMAC512(clientSecret);
        this.refreshTokenExpirySeconds = refreshTokenExpirySeconds;
        this.jwtVerifier = JWT.require(algorithm)
                .withIssuer(issuer)
                .build();
    }

     public String signAccessToken(Claims claims) {
         Date now = new Date();

         return JWT.create()
                 .withIssuer(issuer)
                 .withIssuedAt(now)
                 .withExpiresAt(new Date(now.getTime() + (expirySeconds * 1_000L)))
                 .withClaim(Claims.USER_ID, claims.getId())
                 .withClaim(Claims.USER_NAME, claims.getUsername())
                 .sign(algorithm);
     }

    public String signRefreshToken() {
        Date now = new Date();

        return JWT.create()
                .withIssuer(issuer)
                .withIssuedAt(now)
                .withExpiresAt(new Date(now.getTime() + refreshTokenExpirySeconds * 1_000L))
                .sign(algorithm);
    }

    public boolean isValidFormat(String token) { // 디코딩
        try {
            jwtVerifier.verify(token);
            return true;
        } catch (TokenExpiredException e) {
            return false;
        }
    }

    public Claims getClaims(String token) {
        return new Claims(JWT.decode(token));
    }

    public void verify(String token) {
        jwtVerifier.verify(token);
    }

}
