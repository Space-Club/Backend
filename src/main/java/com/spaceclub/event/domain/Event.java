package com.spaceclub.event.domain;

import com.spaceclub.global.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Event extends BaseTimeEntity {

    @Id
    @Getter
    @Column(name = "event_id")
    @GeneratedValue
    private Long id;

    @Enumerated(EnumType.STRING)
    private Category category;

    @Getter
    @Embedded
    private EventInfo eventInfo;

    @Embedded
    private BankInfo bankInfo;

    @Embedded
    private TicketInfo ticketInfo;

    @Embedded
    private FormInfo formInfo;

    @Getter
    private Long clubId;

    @Builder
    private Event(
            Long id,
            Category category,
            EventInfo eventInfo,
            BankInfo bankInfo,
            TicketInfo ticketInfo,
            FormInfo formInfo,
            Long clubId
    ) {
        this.id = id;
        this.category = category;
        this.eventInfo = eventInfo;
        this.bankInfo = bankInfo;
        this.ticketInfo = ticketInfo;
        this.formInfo = formInfo;
        this.clubId = clubId;
    }

    public String getClubHost() {
        // TODO Club과 연관관계 설정 후 HOST (주최자) 반환하는 메서드
        return "host";
    }

}
