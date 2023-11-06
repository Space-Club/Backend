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

    public static final int MAX_TICKET_COUNT_MIN_LENGTH = 1;

    public static final int MAX_TICKET_COUNT_MAX_LENGTH = 999;

    public static final int COST_MIN_LENGTH = 1;

    public static final int COST_MAX_LENGTH = 1000000;

    @Getter
    private int maxTicketCount;

    @Getter
    private int cost;

    @Builder
    public TicketInfo(int maxTicketCount, int cost) {
        validate(maxTicketCount, cost);
        this.maxTicketCount = maxTicketCount;
        this.cost = cost;
    }

    private void validate(int maxTicketCount, int cost) {
        Assert.isTrue(maxTicketCount >= MAX_TICKET_COUNT_MIN_LENGTH && maxTicketCount <= MAX_TICKET_COUNT_MAX_LENGTH, "인 당 예매 가능 수는 1이상 999이하의 값입니다.");
        Assert.isTrue(cost >= COST_MIN_LENGTH && cost <= COST_MAX_LENGTH, "비용은 1이상 100만원이하의 값입니다.");
    }

}
