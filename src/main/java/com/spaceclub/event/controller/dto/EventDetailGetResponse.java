package com.spaceclub.event.controller.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.spaceclub.event.domain.Event;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record EventDetailGetResponse(
        Long id,
        Long clubId,
        String category,
        EventInfo eventInfo,
        FormInfo formInfo,
        TicketInfo ticketInfo,
        BankInfo bankInfo
) {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private record EventInfo(
            String title,
            String content,
            LocalDate startDate,
            LocalTime startTime,
            LocalDate endDate,
            LocalTime endTime,
            Integer dues,
            String location,
            Integer applicants,
            Integer capacity,
            String posterImageUrl,
            String recruitmentTarget,
            String activityArea
    ) {

        @Builder
        EventInfo {
        }

    }

    private record TicketInfo(
            Integer cost,
            Integer maxTicketCount
    ) {

        @Builder
        TicketInfo {
        }

    }

    private record FormInfo(
            LocalDate formOpenDate,
            LocalTime formOpenTime,
            LocalDate formCloseDate,
            LocalTime formCloseTime
    ) {

        @Builder
        FormInfo {
        }

    }

    private record BankInfo(
            String bankName,
            String bankAccountNumber
    ) {

        @Builder
        BankInfo {
        }

    }

    @Builder
    public EventDetailGetResponse {
    }

    public static EventDetailGetResponse withShow(Event event, Integer applicants) {
        return EventDetailGetResponse.builder()
                .id(event.getId())
                .clubId(event.getClubId())
                .category(event.getCategory().toString())
                .eventInfo(
                        EventInfo.builder()
                                .title(event.getTitle())
                                .content(event.getContent())
                                .startDate(event.getStartDate())
                                .startTime(event.getStartTime())
                                .location(event.getLocation())
                                .applicants(applicants)
                                .capacity(event.getCapacity())
                                .posterImageUrl(event.getPosterImageUrl())
                                .build()
                )
                .ticketInfo(
                        TicketInfo.builder()
                                .cost(event.getCost())
                                .maxTicketCount(event.getMaxTicketCount())
                                .build()
                )
                .formInfo(
                        FormInfo.builder()
                                .formOpenDate(event.getFormOpenDate())
                                .formOpenTime(event.getFormOpenTime())
                                .formCloseDate(event.getFormCloseDate())
                                .formCloseTime(event.getFormCloseTime())
                                .build()
                )
                .bankInfo(BankInfo.builder()
                        .bankName(event.getBankName())
                        .bankAccountNumber(event.getBankAccountNumber())
                        .build()
                )
                .build();
    }

    public static EventDetailGetResponse withClub(Event event, Integer applicants) {
        return EventDetailGetResponse.builder()
                .id(event.getId())
                .clubId(event.getClubId())
                .category(event.getCategory().toString())
                .eventInfo(
                        EventInfo.builder()
                                .title(event.getTitle())
                                .content(event.getContent())
                                .startDate(event.getStartDate())
                                .startTime(event.getStartTime())
                                .endDate(event.getEndDate() == null ? LocalDate.EPOCH : event.getEndDate())
                                .endTime(event.getEndTime() == null ? LocalTime.MIN : event.getEndTime())
                                .dues(event.getDues())
                                .location(event.getLocation())
                                .applicants(applicants)
                                .capacity(event.getCapacity())
                                .posterImageUrl(event.getPosterImageUrl())
                                .build()
                )
                .formInfo(
                        FormInfo.builder()
                                .formOpenDate(event.getFormOpenDate())
                                .formOpenTime(event.getFormOpenTime())
                                .formCloseDate(event.getFormCloseDate())
                                .formCloseTime(event.getFormCloseTime())
                                .build()
                )
                .build();
    }

    public static EventDetailGetResponse withPromotion(Event event, Integer applicants) {
        return EventDetailGetResponse.builder()
                .id(event.getId())
                .clubId(event.getClubId())
                .category(event.getCategory().toString())
                .eventInfo(
                        EventInfo.builder()
                                .title(event.getTitle())
                                .content(event.getContent())
                                .startDate(event.getStartDate())
                                .startTime(event.getStartTime())
                                .applicants(applicants)
                                .capacity(event.getCapacity())
                                .posterImageUrl(event.getPosterImageUrl())
                                .activityArea(event.getActivityArea())
                                .build()
                )
                .formInfo(
                        FormInfo.builder()
                                .formOpenDate(event.getFormOpenDate())
                                .formOpenTime(event.getFormOpenTime())
                                .formCloseDate(event.getFormCloseDate())
                                .formCloseTime(event.getFormCloseTime())
                                .build()
                )
                .build();
    }

    public static EventDetailGetResponse withRecruitment(Event event, Integer applicants) {
        return EventDetailGetResponse.builder()
                .id(event.getId())
                .clubId(event.getClubId())
                .category(event.getCategory().toString())
                .eventInfo(
                        EventInfo.builder()
                                .title(event.getTitle())
                                .content(event.getContent())
                                .startDate(event.getStartDate())
                                .startTime(event.getStartTime())
                                .location(event.getLocation())
                                .applicants(applicants)
                                .capacity(event.getCapacity())
                                .posterImageUrl(event.getPosterImageUrl())
                                .recruitmentTarget(event.getRecruitmentTarget())
                                .build()
                )
                .formInfo(
                        FormInfo.builder()
                                .formOpenDate(event.getFormOpenDate())
                                .formOpenTime(event.getFormOpenTime())
                                .formCloseDate(event.getFormCloseDate())
                                .formCloseTime(event.getFormCloseTime())
                                .build()
                )
                .build();
    }

}
