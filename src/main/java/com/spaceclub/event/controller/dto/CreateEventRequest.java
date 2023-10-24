package com.spaceclub.event.controller.dto;

import com.spaceclub.event.domain.BankInfo;
import com.spaceclub.event.domain.Category;
import com.spaceclub.event.domain.Event;
import com.spaceclub.event.domain.EventInfo;
import com.spaceclub.event.domain.FormInfo;
import com.spaceclub.event.domain.TicketInfo;

import java.time.LocalDate;
import java.time.LocalTime;

public record CreateEventRequest(
        Category category,
        Long clubId,
        EventInfoRequest eventInfo,
        TicketInfoRequest ticketInfo,
        BankInfoRequest bankInfo,
        FormInfoRequest formInfo
) {

    public record EventInfoRequest(
            String title,
            String content,
            LocalDate startDate,
            String location,
            int capacity,
            String poster
    ) {

        public EventInfo toEntity() {
            return EventInfo.builder()
                    .title(title)
                    .content(content)
                    .startDate(startDate)
                    .location(location)
                    .capacity(capacity)
                    .poster(poster)
                    .build();
        }

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
                    .formOpenDate(openDate.atTime(openTime))
                    .formCloseDate(closeDate.atTime(closeTime))
                    .build();
        }

    }

    public Event toEntity() {
        return Event.builder()
                .category(category)
                .clubId(clubId)
                .eventInfo(eventInfo.toEntity())
                .ticketInfo(ticketInfo.toEntity())
                .bankInfo(bankInfo.toEntity())
                .formInfo(formInfo.toEntity())
                .build();
    }

}
