package com.spaceclub.global.jwt;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.Map;
import java.util.StringJoiner;

import static java.util.Optional.ofNullable;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class Claims {

    public static final String USER_NAME = "username";
    public static final String USER_ID = "userId";
    public static final String IAT = "iat";
    public static final String EXP = "exp;";

    @Getter
    private Long id;

    @Getter
    private String username;

    private Date iat;

    private Date exp;

    public static Claims from(Long userId, String username) {
        Claims claims = new Claims();
        claims.id = userId;
        claims.username = username;

        return claims;
    }

    public Claims(DecodedJWT decodedJWT) {
        Claim username = decodedJWT.getClaim(USER_NAME);
        if (!username.isNull()) {
            this.username = username.asString();
        }
        Claim userId = decodedJWT.getClaim(USER_ID);
        if (!userId.isNull()) {
            this.id = userId.asLong();
        }
        this.iat = decodedJWT.getIssuedAt();
        this.exp = decodedJWT.getIssuedAt();
    }

    public Map<String, Object> asMap() {
        return Map.of(
                USER_ID, id,
                USER_NAME, username,
                IAT, iat(),
                EXP, exp()
        );
    }

    private long iat() {
        if (ofNullable(iat).isPresent()) {
            return iat.getTime();
        }

        return -1;
    }

    private long exp() {
        if (ofNullable(exp).isPresent()) {
            return exp.getTime();
        }

        return -1;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Claims.class.getSimpleName() + "[", "]")
                .add("id=" + id)
                .add("username='" + username + "'")
                .add("iat=" + iat)
                .add("exp=" + exp)
                .toString();
    }

}
