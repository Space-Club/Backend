package com.spaceclub.global.oauth.service.vo;

import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.spaceclub.user.domain.User;

import static com.fasterxml.jackson.databind.PropertyNamingStrategies.SnakeCaseStrategy;
import static com.spaceclub.user.domain.Provider.KAKAO;


@JsonNaming(value = SnakeCaseStrategy.class)
public record KakaoUserInfo(
        Long id,
        KakaoAccount kakaoAccount
) {

    public User toUser() {
        return User.builder()
                .oauthId(id.toString())
                .provider(KAKAO)
                .email(kakaoAccount.email)
                .build();
    }

    @JsonNaming(value = SnakeCaseStrategy.class)
    public record KakaoAccount(
            boolean isEmailValid,
            boolean isEmailVerified,
            String email
    ) {

    }

}
