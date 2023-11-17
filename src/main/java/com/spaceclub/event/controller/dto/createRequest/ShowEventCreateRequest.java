package com.spaceclub.event.controller.dto.createRequest;

import com.spaceclub.event.domain.BankInfo;
import com.spaceclub.event.domain.Event;
import com.spaceclub.event.domain.EventCategory;
import com.spaceclub.event.domain.EventInfo;
import com.spaceclub.event.domain.FormInfo;
import com.spaceclub.event.domain.TicketInfo;

import java.time.LocalDate;
import java.time.LocalTime;

public record ShowEventCreateRequest(
        Long clubId,
        EventInfoRequest eventInfo,
        TicketInfoRequest ticketInfo,
        BankInfoRequest bankInfo,
        FormInfoRequest formInfo
) {

    public Event toEntity(EventCategory category, String posterImageUrl) {
        return Event.builder()
                .category(category)
                .eventInfo(eventInfo.toEntity(posterImageUrl))
                .ticketInfo(ticketInfo.toEntity())
                .bankInfo(bankInfo.toEntity())
                .formInfo(formInfo.toEntity())
                .build();
    }

    public record TicketInfoRequest(
            int cost,
            int maxTicketCount
    ) {

        public TicketInfo toEntity() {
            return TicketInfo.builder()
                    .cost(cost)
                    .maxTicketCount(maxTicketCount)
                    .build();
        }

    }

    public record BankInfoRequest(
            String name,
            String accountNumber
    ) {

        public BankInfo toEntity() {
            return BankInfo.builder()
                    .bankName(name)
                    .bankAccountNumber(accountNumber)
                    .build();
        }

    }

    public record FormInfoRequest(
            LocalDate openDate,
            LocalTime openTime,
            LocalDate closeDate,
            LocalTime closeTime
    ) {

        public FormInfo toEntity() {
            return FormInfo.builder()
                    .formOpenDateTime(openDate.atTime(openTime))
                    .formCloseDateTime(closeDate.atTime(closeTime))
                    .build();
        }

    }

    public record EventInfoRequest(
            String title,
            String content,
            LocalDate startDate,
            LocalTime startTime,
            String location,
            int capacity
    ) {

        public EventInfo toEntity(String posterImageUrl) {
            return EventInfo.builder()
                    .title(title)
                    .content(content)
                    .startDateTime(startDate.atTime(startTime))
                    .location(location)
                    .capacity(capacity)
                    .posterImageUrl(posterImageUrl)
                    .build();
        }

    }

}
