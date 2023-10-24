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
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
//@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Event extends BaseTimeEntity {

    @Id
    @Getter
    @Column(name = "event_id")
    @GeneratedValue
    private Long id;

    @Enumerated(EnumType.STRING)
    private Category category;

    @Embedded
    private EventInfo eventInfo;

    @Embedded
    private BankInfo bankInfo;

    @Embedded
    private TicketInfo ticketInfo;

    @Embedded
    private FormInfo formInfo;

    private Long clubId;

    public Event() {}

}
