package com.spaceclub.event.domain;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TicketInfo {

    private int maxTicketCount;

    private int cost;

    @Builder
    public TicketInfo(int maxTicketCount, int cost) {
        this.maxTicketCount = maxTicketCount;
        this.cost = cost;
    }

}
