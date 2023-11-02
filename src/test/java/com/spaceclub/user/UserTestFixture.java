package com.spaceclub.user;

import com.spaceclub.user.domain.Email;
import com.spaceclub.user.domain.Provider;
import com.spaceclub.user.domain.User;

public class UserTestFixture {

    public static User user() {
        return User.builder()
                .id(1L)
                .nickname("nickname1")
                .phoneNumber("010-1234-5678")
                .oauthUserName("1234")
                .provider(Provider.KAKAO)
                .email(new Email("abc@naver.com"))
                .refreshToken("refreshToken")
                .build();
    }

}
