package com.spaceclub.event.controller.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.spaceclub.event.domain.BankInfo;
import com.spaceclub.event.domain.Event;
import com.spaceclub.event.domain.EventCategory;
import com.spaceclub.event.domain.EventInfo;
import com.spaceclub.event.domain.FormInfo;
import com.spaceclub.event.domain.TicketInfo;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record EventUpdateRequest(
        Long eventId,
        EventInfoRequest eventInfo,
        FormInfoRequest formInfo,
        TicketInfoRequest ticketInfo,
        BankInfoRequest bankInfo
) {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record EventInfoRequest(
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
        public EventInfoRequest {
        }

        private EventInfo toEntity() {
            return EventInfo.builder()
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

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record TicketInfoRequest(
            Integer cost,
            Integer maxTicketCount
    ) {

        @Builder
        public TicketInfoRequest {
        }

        private TicketInfo toEntity() {
            return TicketInfo.builder()
                    .cost(cost)
                    .maxTicketCount(maxTicketCount)
                    .build();
        }

    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record FormInfoRequest(
            LocalDate openDate,
            LocalTime openTime,
            LocalDate closeDate,
            LocalTime closeTime,
            Boolean isAbleToApply
    ) {

        @Builder
        public FormInfoRequest {
        }

        private FormInfo toEntity() {
            return FormInfo.builder()
                    .formOpenDateTime(openDate.atTime(openTime))
                    .formCloseDateTime(closeDate.atTime(closeTime))
                    .build();
        }

    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public record BankInfoRequest(
            String name,
            String accountNumber
    ) {

        @Builder
        public BankInfoRequest {
        }

        private BankInfo toEntity() {
            return BankInfo.builder()
                    .bankName(name)
                    .bankAccountNumber(accountNumber)
                    .build();
        }

    }

    @Builder
    public EventUpdateRequest {
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
                .eventInfo(eventInfo != null ? eventInfo.toEntity() : null)
                .ticketInfo(ticketInfo != null ? ticketInfo.toEntity() : null)
                .formInfo(formInfo != null ? formInfo.toEntity() : null)
                .bankInfo(bankInfo != null ? bankInfo.toEntity() : null)
                .build();
    }

    private Event withClub() {
        return Event.builder()
                .id(eventId)
                .category(EventCategory.CLUB)
                .eventInfo(eventInfo != null ? eventInfo.toEntity() : null)
                .formInfo(formInfo != null ? formInfo.toEntity() : null)
                .build();
    }

    private Event withPromotion() {
        return Event.builder()
                .id(eventId)
                .category(EventCategory.PROMOTION)
                .eventInfo(eventInfo != null ? eventInfo.toEntity() : null)
                .formInfo(formInfo != null ? formInfo.toEntity() : null)
                .build();
    }

    private Event withRecruitment() {
        return Event.builder()
                .id(eventId)
                .category(EventCategory.RECRUITMENT)
                .eventInfo(eventInfo != null ? eventInfo.toEntity() : null)
                .formInfo(formInfo != null ? formInfo.toEntity() : null)
                .build();
    }

}
