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
public record EventCreateRequest(
        Long clubId,
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

        public static final int MAX_CAPACITY = 1000;

        @Builder
        public EventInfoRequest {
        }

        private EventInfo toEntity(FormInfoRequest formInfoRequest) {
            return EventInfo.builder()
                    .title(title)
                    .content(content)
                    .startDateTime(startDate != null ? startDate.atTime(startTime) : formInfoRequest.openDate.atTime(formInfoRequest.openTime))
                    .endDateTime(endDate != null ? endDate.atTime(endTime) : formInfoRequest.closeDate.atTime(formInfoRequest.closeTime))
                    .dues(dues)
                    .location(location)
                    .capacity(capacity != null ? capacity : MAX_CAPACITY)
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
