package com.spaceclub.global.jwt;

import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;
import java.util.Date;
import java.util.Map;
import java.util.StringJoiner;

import static java.util.Optional.ofNullable;
import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public class Claims {

    public static final String USER_NAME = "username";
    public static final String USER_ROLE = "roles";
    public static final String IAT = "iat";
    public static final String EXP = "exp;";

    @Getter
    private String username;

    @Getter
    private String[] roles;

    private Date iat;

    private Date exp;

    public static Claims from(String username, String[] roles) {
        Claims claims = new Claims();
        claims.username = username;
        claims.roles = roles;

        return claims;
    }

    public Claims(DecodedJWT decodedJWT) {
        Claim username = decodedJWT.getClaim(USER_NAME);
        if (!username.isNull()) {
            this.username = username.asString();
        }
        Claim roles = decodedJWT.getClaim(USER_ROLE);
        if (!roles.isNull()) {
            this.roles = roles.asArray(String.class);
        }
        this.iat = decodedJWT.getIssuedAt();
        this.exp = decodedJWT.getIssuedAt();
    }

    public Map<String, Object> asMap() {
        return Map.of(
                USER_NAME, username,
                USER_ROLE, roles,
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
                .add("username='" + username + "'")
                .add("roles=" + Arrays.toString(roles))
                .add("iat=" + iat)
                .add("exp=" + exp)
                .toString();
    }

}
