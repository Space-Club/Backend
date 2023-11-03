package com.spaceclub.user;

import com.spaceclub.user.domain.Provider;
import com.spaceclub.user.domain.User;

<<<<<<< HEAD
import static com.spaceclub.user.domain.Status.REGISTERED;
=======
import static com.spaceclub.user.domain.Status.*;
>>>>>>> 672ce18 (feat: User 객체 신규 회원 검증 로직 추가 및 테스트)

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
                .email("abcd@naver.com")
                .refreshToken("refreshToken")
                .profileImageUrl("www.image.com")
                .build();
    }

}
