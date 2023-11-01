package com.spaceclub.event;

import com.spaceclub.event.domain.BankInfo;
import com.spaceclub.event.domain.Category;
import com.spaceclub.event.domain.Event;
import com.spaceclub.event.domain.EventInfo;
import com.spaceclub.event.domain.FormInfo;
import com.spaceclub.event.domain.TicketInfo;

import java.time.LocalDateTime;

import static com.spaceclub.club.ClubTestFixture.club1;

public class EventTestFixture {

    public static EventInfo eventInfo() {
        return EventInfo.builder()
                .title("제목")
                .content("내용")
                .startDate(LocalDateTime.of(2023, 9, 21, 12, 30, 30))
                .location("위치")
                .capacity(100)
                .poster("www.aaa.com")
                .build();
    }

    public static BankInfo bankInfo() {
        return BankInfo.builder()
                .bankName("은행명")
                .bankAccountNumber("계좌번호")
                .build();
    }

    public static TicketInfo ticketInfo() {
        return TicketInfo.builder()
                .maxTicketCount(4)
                .cost(3000)
                .build();
    }

    public static FormInfo formInfo() {
        return FormInfo.builder()
                .formOpenDate(LocalDateTime.of(2023, 10, 24, 23, 41, 30))
                .formCloseDate(LocalDateTime.of(2023, 10, 24, 23, 41, 30))
                .build();
    }

    public static Event event1() {
        return Event.builder()
                .id(1L)
                .category(Category.SHOW)
                .eventInfo(eventInfo())
                .ticketInfo(ticketInfo())
                .bankInfo(bankInfo())
                .formInfo(formInfo())
                .club(club1())
                .build();
    }

    public static Event event2() {
        return Event.builder()
                .id(2L)
                .category(Category.SHOW)
                .eventInfo(eventInfo())
                .ticketInfo(ticketInfo())
                .bankInfo(bankInfo())
                .formInfo(formInfo())
                .club(club1())
                .build();
    }

    public static Event event3() {
        return Event.builder()
                .id(3L)
                .category(Category.CLUB)
                .eventInfo(eventInfo())
                .ticketInfo(ticketInfo())
                .bankInfo(bankInfo())
                .formInfo(formInfo())
                .club(club1())
                .build();
    }

}
