package com.spaceclub.club.controller.dto;

import com.spaceclub.club.domain.ClubUser;
import com.spaceclub.club.domain.ClubUserRole;
import com.spaceclub.user.domain.Provider;
import com.spaceclub.user.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.List;

import static com.spaceclub.user.domain.Status.REGISTERED;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class MemberGetResponseTest {

    private User user1() {
        return User.builder()
                .id(1L)
                .name("가방")
                .phoneNumber("010-1234-5678")
                .status(REGISTERED)
                .oauthId("12345678")
                .provider(Provider.KAKAO)
                .email("abc@naver.com")
                .refreshToken("refreshToken")
                .profileImageUrl("www.image.com")
                .build();
    }

    private User user2() {
        return User.builder()
                .id(2L)
                .name("가지")
                .phoneNumber("010-1234-5678")
                .status(REGISTERED)
                .oauthId("1234")
                .provider(Provider.KAKAO)
                .email("abcd@naver.com")
                .refreshToken("refreshToken")
                .profileImageUrl("www.image.com")
                .build();
    }

    private User user3() {
        return User.builder()
                .id(3L)
                .name("하지")
                .phoneNumber("010-1234-5678")
                .status(REGISTERED)
                .oauthId("1234")
                .provider(Provider.KAKAO)
                .email("abcd@naver.com")
                .refreshToken("refreshToken")
                .profileImageUrl("www.image.com")
                .build();
    }

    private User user4() {
        return User.builder()
                .id(4L)
                .name("하지")
                .phoneNumber("010-1234-5678")
                .status(REGISTERED)
                .oauthId("1234")
                .provider(Provider.KAKAO)
                .email("abcd@naver.com")
                .refreshToken("refreshToken")
                .profileImageUrl("www.image.com")
                .build();
    }

    @Test
    void 클럽_멤버_정렬에_성공한다() {
        // given
        ClubUser clubUserOrder1 = ClubUser.builder()
                .id(1L)
                .user(user1())
                .role(ClubUserRole.MANAGER)
                .build();

        ClubUser clubUserOrder2 = ClubUser.builder()
                .id(2L)
                .user(user2())
                .role(ClubUserRole.MEMBER)
                .build();

        ClubUser clubUserOrder3 = ClubUser.builder()
                .id(3L)
                .user(user3())
                .role(ClubUserRole.MANAGER)
                .build();

        ClubUser clubUserOrder4 = ClubUser.builder()
                .id(4L)
                .user(user4())
                .role(ClubUserRole.MEMBER)
                .build();

        List<ClubUser> response = new ArrayList<>();
        response.add(clubUserOrder1);
        response.add(clubUserOrder2);
        response.add(clubUserOrder3);
        response.add(clubUserOrder4);

        // when
        response.sort(ClubUser.memberComparator);

        // then
        assertThat(response).containsExactly(clubUserOrder1, clubUserOrder3, clubUserOrder2, clubUserOrder4);
    }

}
