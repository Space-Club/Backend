package com.spaceclub.global.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import lombok.Builder;

import java.util.Date;

public class Jwt {

    private final String issuer;

    private final String clientSecret;

    private final int expirySeconds;

    private final Algorithm algorithm;

    private final JWTVerifier jwtVerifier;

    @Builder
    private Jwt(
            String issuer,
            String clientSecret,
            int expirySeconds
    ) {
        this.issuer = issuer;
        this.clientSecret = clientSecret;
        this.expirySeconds = expirySeconds;
        this.algorithm = Algorithm.HMAC512(clientSecret);
        this.jwtVerifier = JWT.require(algorithm)
                .withIssuer(issuer)
                .build();
    }

     public String sign(Claims claims) { // 토큰 생성
         Date now = new Date();
         JWTCreator.Builder builder = JWT.create();
         builder.withIssuer(issuer);
         builder.withIssuedAt(now);
         if (expirySeconds > 0) {
             builder.withExpiresAt(new Date(now.getTime() + (expirySeconds * 1_000L)));
         }
         builder.withClaim(Claims.USER_NAME, claims.getUsername());
         builder.withArrayClaim(Claims.USER_ROLE, claims.getRoles());
         return builder.sign(algorithm);
     }

     public Claims verify(String token) { // 디코딩
        return new Claims(jwtVerifier.verify(token));
     }

}
