package com.spaceclub.club.controller.dto;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static com.spaceclub.event.EventTestFixture.event1;
import static com.spaceclub.event.EventTestFixture.event_club;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DataJpaTest
class ClubEventGetResponseTest {

    @Test
    void 클럽의_모든_이벤트_조회에_성공한다() {
        // given
        ClubEventGetResponse showCategory = ClubEventGetResponse.from(event1());
        ClubEventGetResponse clubCategory = ClubEventGetResponse.from(event_club());

        // when, then
        assertAll(
                () -> assertThat(showCategory.openStatus())
                        .isEqualTo("ALL"),
                () -> assertThat(clubCategory.openStatus())
                        .isEqualTo("CLUB")
        );
    }

}
