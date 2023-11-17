package com.spaceclub.event;

import com.spaceclub.event.domain.ApplicationStatus;
import com.spaceclub.event.domain.BankInfo;
import com.spaceclub.event.domain.Event;
import com.spaceclub.event.domain.EventInfo;
import com.spaceclub.event.domain.EventUser;
import com.spaceclub.event.domain.FormInfo;
import com.spaceclub.event.domain.TicketInfo;

import java.time.LocalDateTime;

import static com.spaceclub.club.ClubTestFixture.club1;
import static com.spaceclub.event.domain.EventCategory.CLUB;
import static com.spaceclub.event.domain.EventCategory.SHOW;
import static com.spaceclub.user.UserTestFixture.user1;

public class EventTestFixture {

    public static EventInfo eventInfo() {
        return EventInfo.builder()
                .title("제목")
                .content("내용")
                .startDateTime(LocalDateTime.of(2023, 9, 21, 12, 30, 30))
                .location("위치")
                .capacity(100)
                .posterImageUrl("www.aaa.com")
                .managerName("행사 생성자 이름")
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
                .category(SHOW)
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
                .category(SHOW)
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
                .category(CLUB)
                .eventInfo(eventInfo())
                .ticketInfo(ticketInfo())
                .bankInfo(bankInfo())
                .formInfo(formInfo())
                .club(club1())
                .build();
    }

    public static EventUser eventUser() {
        return EventUser.builder()
                .id(1L)
                .status(ApplicationStatus.PENDING)
                .user(user1())
                .event(event1())
                .build();
    }

}
