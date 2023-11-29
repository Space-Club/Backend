package com.spaceclub.event.controller.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.spaceclub.event.domain.Event;
import com.spaceclub.event.domain.EventCategory;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record EventCreateRequest(
        Long clubId,
        EventInfo eventInfo,
        FormInfo formInfo,
        TicketInfo ticketInfo,
        BankInfo bankInfo
) {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record EventInfo(
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
        public EventInfo {
        }

        private com.spaceclub.event.domain.EventInfo toEntity(FormInfo formInfo) {
            return com.spaceclub.event.domain.EventInfo.builder()
                    .title(title)
                    .content(content)
                    .startDateTime(startDate != null ? startDate.atTime(startTime) : formInfo.openDate.atTime(formInfo.openTime))
                    .endDateTime(endDate != null ? endDate.atTime(endTime) : formInfo.closeDate.atTime(formInfo.closeTime))
                    .dues(dues)
                    .location(location)
                    .capacity(capacity)
                    .recruitmentTarget(recruitmentTarget)
                    .activityArea(activityArea)
                    .recruitmentLimit(recruitmentLimit)
                    .build();
        }

    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record TicketInfo(
            Integer cost,
            Integer maxTicketCount
    ) {

        @Builder
        public TicketInfo {
        }

        private com.spaceclub.event.domain.TicketInfo toEntity() {
            return com.spaceclub.event.domain.TicketInfo.builder()
                    .cost(cost)
                    .maxTicketCount(maxTicketCount)
                    .build();
        }

    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record FormInfo(
            LocalDate openDate,
            LocalTime openTime,
            LocalDate closeDate,
            LocalTime closeTime,
            Boolean isAbleToApply
    ) {

        @Builder
        public FormInfo {
        }

        private com.spaceclub.event.domain.FormInfo toEntity() {
            return com.spaceclub.event.domain.FormInfo.builder()
                    .formOpenDateTime(openDate.atTime(openTime))
                    .formCloseDateTime(closeDate.atTime(closeTime))
                    .build();
        }

    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record BankInfo(
            String name,
            String accountNumber
    ) {

        @Builder
        public BankInfo {
        }

        private com.spaceclub.event.domain.BankInfo toEntity() {
            return com.spaceclub.event.domain.BankInfo.builder()
                    .bankName(name)
                    .bankAccountNumber(accountNumber)
                    .build();
        }

    }

    @Builder
    public EventCreateRequest {
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
                .category(EventCategory.SHOW)
                .eventInfo(eventInfo.toEntity(formInfo))
                .ticketInfo(ticketInfo.toEntity())
                .formInfo(formInfo.toEntity())
                .bankInfo(bankInfo.toEntity())
                .build();
    }

    private Event withClub() {
        return Event.builder()
                .category(EventCategory.CLUB)
                .eventInfo(eventInfo.toEntity(formInfo))
                .formInfo(formInfo.toEntity())
                .build();
    }

    private Event withPromotion() {
        return Event.builder()
                .category(EventCategory.PROMOTION)
                .eventInfo(eventInfo.toEntity(formInfo))
                .formInfo(formInfo.toEntity())
                .build();
    }

    private Event withRecruitment() {
        return Event.builder()
                .category(EventCategory.RECRUITMENT)
                .eventInfo(eventInfo.toEntity(formInfo))
                .formInfo(formInfo.toEntity())
                .build();
    }

}
