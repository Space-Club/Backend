package com.spaceclub.user;

import com.spaceclub.user.domain.Provider;
import com.spaceclub.user.domain.Status;
import com.spaceclub.user.domain.User;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.spaceclub.user.domain.Status.NOT_REGISTERED;
import static com.spaceclub.user.domain.Status.REGISTERED;

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

    public static User notRegisteredUser() {
        return User.builder()
                .id(3L)
                .name(null)
                .phoneNumber(null)
                .status(NOT_REGISTERED)
                .oauthId("1234")
                .provider(Provider.KAKAO)
                .email("abce@naver.com")
                .refreshToken("refreshToken")
                .profileImageUrl("www.image.com")
                .build();
    }

    public static User deletedUser(Long userId, Status status, LocalDateTime deletedAt) {
        UUID uuid = UUID.randomUUID();
        String emailPrefix = uuid.toString().substring(5);
        String oauthId = uuid.toString().substring(0, 5);

        return User.builder()
                .id(userId)
                .name(null)
                .phoneNumber(null)
                .status(status)
                .oauthId(oauthId)
                .provider(Provider.KAKAO)
                .email(emailPrefix+ "@naver.com")
                .refreshToken("refreshToken")
                .profileImageUrl("www.image.com")
                .deletedAt(deletedAt)
                .build();
    }

}
