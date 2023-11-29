package com.spaceclub.event.controller.dto;

import com.spaceclub.event.domain.Event;
import com.spaceclub.event.domain.EventCategory;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalTime;

public record EventUpdateRequest(
        Long eventId,
        EventInfo eventInfo,
        FormInfo formInfo,
        TicketInfo ticketInfo,
        BankInfo bankInfo
) {

    private record EventInfo(
            String title,
            String content,
            LocalDate startDate,
            LocalTime startTime,
            LocalDate endDate,
            LocalTime endTime,
            Integer dues,
            String location,
            Integer capacity,
            String recruitmentTarget,
            String activityArea,
            Integer recruitmentLimit
    ) {

        @Builder
        EventInfo {
        }

        private com.spaceclub.event.domain.EventInfo toEntity(FormInfo formInfo) {
            return com.spaceclub.event.domain.EventInfo.builder()
                    .title(title)
                    .content(content)
                    .startDateTime(startDate != null ? startDate.atTime(startTime) : null)
                    .endDateTime(endDate != null ? endDate.atTime(endTime) : null)
                    .dues(dues)
                    .location(location)
                    .capacity(capacity)
                    .recruitmentTarget(recruitmentTarget)
                    .activityArea(activityArea)
                    .recruitmentLimit(recruitmentLimit)
                    .build();
        }

    }

    private record TicketInfo(
            Integer cost,
            Integer maxTicketCount
    ) {

        @Builder
        TicketInfo {
        }

        private com.spaceclub.event.domain.TicketInfo toEntity() {
            return com.spaceclub.event.domain.TicketInfo.builder()
                    .cost(cost)
                    .maxTicketCount(maxTicketCount)
                    .build();
        }

    }

    private record FormInfo(
            LocalDate openDate,
            LocalTime openTime,
            LocalDate closeDate,
            LocalTime closeTime,
            Boolean isAbleToApply
    ) {

        @Builder
        FormInfo {
        }

        private com.spaceclub.event.domain.FormInfo toEntity() {
            return com.spaceclub.event.domain.FormInfo.builder()
                    .formOpenDateTime(openDate.atTime(openTime))
                    .formCloseDateTime(closeDate.atTime(closeTime))
                    .build();
        }

    }

    private record BankInfo(
            String name,
            String accountNumber
    ) {

        @Builder
        BankInfo {
        }

        private com.spaceclub.event.domain.BankInfo toEntity() {
            return com.spaceclub.event.domain.BankInfo.builder()
                    .bankName(name)
                    .bankAccountNumber(accountNumber)
                    .build();
        }

    }

    public Event toEntity(EventCategory eventCategory) {
        return switch (eventCategory) {
            case SHOW -> withShow();
            case CLUB -> withClub();
            case PROMOTION -> withPromotion();
            case RECRUITMENT -> withRecruitment();
        };
    }

    private Event withShow() {
        return Event.builder()
                .id(eventId)
                .category(EventCategory.SHOW)
                .eventInfo(eventInfo != null ? eventInfo.toEntity(formInfo) : null)
                .ticketInfo(ticketInfo != null ? ticketInfo.toEntity() : null)
                .formInfo(formInfo != null ? formInfo.toEntity() : null)
                .bankInfo(bankInfo != null ? bankInfo.toEntity() : null)
                .build();
    }

    private Event withClub() {
        return Event.builder()
                .id(eventId)
                .category(EventCategory.CLUB)
                .eventInfo(eventInfo != null ? eventInfo.toEntity(formInfo) : null)
                .formInfo(formInfo != null ? formInfo.toEntity() : null)
                .build();
    }

    private Event withPromotion() {
        return Event.builder()
                .id(eventId)
                .category(EventCategory.PROMOTION)
                .eventInfo(eventInfo != null ? eventInfo.toEntity(formInfo) : null)
                .formInfo(formInfo != null ? formInfo.toEntity() : null)
                .build();
    }

    private Event withRecruitment() {
        return Event.builder()
                .id(eventId)
                .category(EventCategory.RECRUITMENT)
                .eventInfo(eventInfo != null ? eventInfo.toEntity(formInfo) : null)
                .formInfo(formInfo != null ? formInfo.toEntity() : null)
                .build();
    }

}
