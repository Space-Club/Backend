package com.spaceclub.event;

import com.spaceclub.event.domain.BankInfo;
import com.spaceclub.event.domain.Event;
import com.spaceclub.event.domain.EventInfo;
import com.spaceclub.event.domain.EventUser;
import com.spaceclub.event.domain.FormInfo;
import com.spaceclub.event.domain.ParticipationStatus;
import com.spaceclub.event.domain.TicketInfo;

import java.time.LocalDateTime;

import static com.spaceclub.club.ClubTestFixture.club1;
import static com.spaceclub.event.domain.EventCategory.CLUB;
import static com.spaceclub.event.domain.EventCategory.PROMOTION;
import static com.spaceclub.event.domain.EventCategory.RECRUITMENT;
import static com.spaceclub.event.domain.EventCategory.SHOW;

public class EventTestFixture {

    public static EventInfo eventInfo() {
        return EventInfo.builder()
                .title("제목")
                .content("내용")
                .startDateTime(LocalDateTime.of(2023, 9, 21, 12, 30, 30))
                .endDateTime(LocalDateTime.of(2024, 9, 21, 12, 30, 30))
                .location("위치")
                .capacity(100)
                .posterImageUrl("www.aaa.com")
                .managerName("행사 생성자 이름")
                .dues(5000)
                .recruitmentTarget("연어를 좋아하는 사람 누구나")
                .activityArea("홍대입구역 근처")
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
                .formOpenDateTime(LocalDateTime.of(2023, 10, 24, 23, 41, 30))
                .formCloseDateTime(LocalDateTime.of(2023, 10, 24, 23, 41, 30))
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

    public static Event showEvent() {
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

    public static Event clubEvent() {
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

    public static Event promotionEvent() {
        return Event.builder()
                .id(4L)
                .category(PROMOTION)
                .eventInfo(eventInfo())
                .ticketInfo(ticketInfo())
                .bankInfo(bankInfo())
                .formInfo(formInfo())
                .club(club1())
                .build();
    }

    public static Event recruitmentEvent() {
        return Event.builder()
                .id(5L)
                .category(RECRUITMENT)
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
                .status(ParticipationStatus.PENDING)
                .userId(1L)
                .event(event1())
                .build();
    }

}
