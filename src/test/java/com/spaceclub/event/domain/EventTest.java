package com.spaceclub.event.domain;

import com.spaceclub.SpaceClubCustomDisplayNameGenerator;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;

import static com.spaceclub.club.ClubTestFixture.club1;
import static com.spaceclub.event.EventTestFixture.bankInfo;
import static com.spaceclub.event.EventTestFixture.event1;
import static com.spaceclub.event.EventTestFixture.eventInfo;
import static com.spaceclub.event.EventTestFixture.formInfo;
import static com.spaceclub.event.EventTestFixture.ticketInfo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayNameGeneration(SpaceClubCustomDisplayNameGenerator.class)
class EventTest {

    @Test
    void 이벤트_생성에_성공한다() {
        assertThat(event1()).isNotNull();
    }

    @Test
    void 카테고리가_null이면_생성에_실패한다() {
        assertThatThrownBy(() ->
                Event.builder()
                        .id(1L)
                        .category(null)
                        .eventInfo(eventInfo())
                        .ticketInfo(ticketInfo())
                        .bankInfo(bankInfo())
                        .formInfo(formInfo())
                        .club(club1())
                        .build())
                .isInstanceOf(IllegalArgumentException.class);
    }

}
