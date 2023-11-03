package com.spaceclub.user;

import com.spaceclub.user.domain.Provider;
import com.spaceclub.user.domain.User;

import static com.spaceclub.user.domain.Status.*;

public class UserTestFixture {

    public static User user1() {
        return User.builder()
                .id(1L)
                .name("멤버명")
                .phoneNumber("010-1234-5678")
                .status(REGISTERED)
                .oauthId("12345678")
                .provider(Provider.KAKAO)
                .email("abc@naver.com")
                .refreshToken("refreshToken")
                .profileImageUrl("www.image.com")
                .build();
    }

    public static User user2() {
        return User.builder()
                .id(2L)
                .name("멤버명")
                .phoneNumber("010-1234-5678")
                .status(REGISTERED)
                .oauthId("1234")
                .provider(Provider.KAKAO)
                .email("abc@naver.com")
                .refreshToken("refreshToken")
                .profileImageUrl("www.image.com")
                .build();
    }

}
