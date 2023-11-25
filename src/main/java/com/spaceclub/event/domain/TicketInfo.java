package com.spaceclub.event.domain;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

import static com.spaceclub.event.EventExceptionMessage.INVALID_EVENT_COST;
import static com.spaceclub.event.EventExceptionMessage.INVALID_EVENT_MAX_TICKET_COUNT;

@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TicketInfo {

    private static final Integer MAX_TICKET_COUNT_MIN_COUNT = 1;

    private static final Integer MAX_TICKET_COUNT_MAX_COUNT = 999;

    private static final Integer COST_MIN_LENGTH = 1;

    private static final Integer COST_MAX_LENGTH = 1000000;

    @Getter
    private Integer maxTicketCount;

    @Getter
    private Integer cost;

    @Builder
    public TicketInfo(Integer maxTicketCount, Integer cost) {
        validate(maxTicketCount, cost);
        this.maxTicketCount = maxTicketCount;
        this.cost = cost;
    }

    private void validate(Integer maxTicketCount, Integer cost) {

        if (maxTicketCount != null) {
            boolean validateMaxTicketCountRange = maxTicketCount >= MAX_TICKET_COUNT_MIN_COUNT && maxTicketCount <= MAX_TICKET_COUNT_MAX_COUNT;
            Assert.isTrue(validateMaxTicketCountRange, INVALID_EVENT_MAX_TICKET_COUNT.toString());
        }

        if (cost != null) {
            boolean validateCostRange = cost >= COST_MIN_LENGTH && cost <= COST_MAX_LENGTH;
            Assert.isTrue(validateCostRange, INVALID_EVENT_COST.toString());
        }

    }

}
