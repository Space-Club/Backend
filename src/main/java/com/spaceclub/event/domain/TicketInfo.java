package com.spaceclub.event.domain;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

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
        Assert.isTrue(maxTicketCount >= MAX_TICKET_COUNT_MIN_COUNT && maxTicketCount <= MAX_TICKET_COUNT_MAX_COUNT, "인 당 예매 가능 수는 1이상 999이하의 값입니다.");
        Assert.isTrue(cost >= COST_MIN_LENGTH && cost <= COST_MAX_LENGTH, "비용은 1이상 100만원이하의 값입니다.");
    }

}
