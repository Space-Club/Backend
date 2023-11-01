package com.spaceclub.user;

import com.spaceclub.user.domain.Email;
import com.spaceclub.user.domain.Provider;
import com.spaceclub.user.domain.User;

public class UserTestFixture {

    public static User user1() {
        return User.builder()
                .id(1L)
                .nickname("멤버명")
                .phoneNumber("010-1234-5678")
                .oauthUserName("1234")
                .provider(Provider.KAKAO)
                .email(new Email("abc@naver.com"))
                .refreshToken("refreshToken")
                .image("www.image.com")
                .build();
    }

    public static User user2() {
        return User.builder()
                .id(2L)
                .nickname("멤버명")
                .phoneNumber("010-1234-5678")
                .oauthUserName("12345")
                .provider(Provider.KAKAO)
                .email(new Email("abc@naver.com"))
                .refreshToken("refreshToken")
                .image("www.image.com")
                .build();
    }

}
