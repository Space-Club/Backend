package com.spaceclub.user;

import com.spaceclub.user.domain.Provider;
import com.spaceclub.user.domain.User;

public class UserTestFixture {

    public static User user1() {
        return User.builder()
                .id(1L)
                .name("멤버명")
                .phoneNumber("010-1234-5678")
                .oauthId("1234")
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
                .oauthId("12345")
                .provider(Provider.KAKAO)
                .email("abc@naver.com")
                .refreshToken("refreshToken")
                .profileImageUrl("www.image.com")
                .build();
    }

}
