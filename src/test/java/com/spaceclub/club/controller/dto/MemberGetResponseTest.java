package com.spaceclub.club.controller.dto;

import com.spaceclub.club.domain.ClubUserRole;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class MemberGetResponseTest {

    @Test
    void 클럽_멤버_정렬에_성공한다() {
        // given
        MemberGetResponse responseOrder1 = MemberGetResponse.builder()
                .id(1L)
                .name("가방")
                .profileImageUrl("www.aaa.com")
                .role(ClubUserRole.MANAGER)
                .build();

        MemberGetResponse responseOrder2 = MemberGetResponse.builder()
                .id(2L)
                .name("가지")
                .profileImageUrl("www.aaa.com")
                .role(ClubUserRole.MEMBER)
                .build();

        MemberGetResponse responseOrder3 = MemberGetResponse.builder()
                .id(3L)
                .name("하지")
                .profileImageUrl("www.aaa.com")
                .role(ClubUserRole.MANAGER)
                .build();

        MemberGetResponse responseOrder4 = MemberGetResponse.builder()
                .id(4L)
                .name("하지")
                .profileImageUrl("www.aaa.com")
                .role(ClubUserRole.MEMBER)
                .build();

        List<MemberGetResponse> response = new ArrayList<>();
        response.add(responseOrder4);
        response.add(responseOrder2);
        response.add(responseOrder1);
        response.add(responseOrder3);

        // when
        response.sort(MemberGetResponse.memberComparator);

        // then
        assertThat(response).containsExactly(responseOrder1, responseOrder3, responseOrder2, responseOrder4);
    }

}
