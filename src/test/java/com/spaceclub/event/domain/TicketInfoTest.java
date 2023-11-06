package com.spaceclub.event.domain;

import com.spaceclub.SpaceClubCustomDisplayNameGenerator;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static com.spaceclub.event.EventTestFixture.ticketInfo;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayNameGeneration(SpaceClubCustomDisplayNameGenerator.class)
class TicketInfoTest {

    @Test
    void 티켓_정보_생성에_성공한다() {
        assertThat(ticketInfo()).isNotNull();
    }

    @ValueSource(ints = {0, 1000001})
    @ParameterizedTest(name = "{index}. cost : [{arguments}]")
    void 비용이_유효하지_않으면_생성에_실패한다(int cost) {
        assertThatThrownBy(() ->
                TicketInfo.builder()
                        .cost(cost)
                        .maxTicketCount(ticketInfo().getMaxTicketCount())
                        .build())
                .isInstanceOf(IllegalArgumentException.class);
    }

    @ValueSource(ints = {0, 1000})
    @ParameterizedTest(name = "{index}. BankName : [{arguments}]")
    void 인_당_예매_가능수가_유효하지_않으면_생성에_실패한다(int maxTicketCount) {
        assertThatThrownBy(() ->
                TicketInfo.builder()
                        .cost(ticketInfo().getCost())
                        .maxTicketCount(maxTicketCount)
                        .build())
                .isInstanceOf(IllegalArgumentException.class);
    }

}
