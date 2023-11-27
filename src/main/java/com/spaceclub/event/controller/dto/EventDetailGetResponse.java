package com.spaceclub.event.controller.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.spaceclub.event.domain.Event;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record EventDetailGetResponse(
        Long id,
        String category,
        boolean hasForm,
        EventInfo eventInfo,
        FormInfo formInfo,
        TicketInfo ticketInfo,
        BankInfo bankInfo,
        ClubInfo clubInfo
) {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private record EventInfo(
            String title,
            String content,
            LocalDate startDate,
            LocalTime startTime,
            LocalDate endDate,
            LocalTime endTime,
            boolean isEnded,
            Integer dues,
            String location,
            int applicants,
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
            LocalDate openDate,
            LocalTime openTime,
            LocalDate closeDate,
            LocalTime closeTime,
            Boolean isAbleToApply
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

    private record ClubInfo(
            Long clubId,
            String clubName
    ) {

        @Builder
        private ClubInfo {
        }

    }


    @Builder
    public EventDetailGetResponse {
    }

    public static EventDetailGetResponse withShow(Event event, int applicants, String bucketUrl) {
        return EventDetailGetResponse.builder()
                .id(event.getId())
                .category(event.getCategory().toString())
                .hasForm(event.hasForm())
                .eventInfo(
                        EventInfo.builder()
                                .title(event.getTitle())
                                .content(event.getContent())
                                .startDate(event.getStartDate())
                                .startTime(event.getStartTime())
                                .isEnded(event.isEventEnded())
                                .location(event.getLocation())
                                .applicants(applicants)
                                .capacity(event.getCapacity())
                                .posterImageUrl(event.getPosterImageName() != null ? bucketUrl + event.getPosterImageName() : null)
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
                                .openDate(event.getFormOpenDate())
                                .openTime(event.getFormOpenTime())
                                .closeDate(event.getFormCloseDate())
                                .closeTime(event.getFormCloseTime())
                                .isAbleToApply(event.isAbleToApply())
                                .build()
                )
                .bankInfo(BankInfo.builder()
                        .bankName(event.getBankName())
                        .bankAccountNumber(event.getBankAccountNumber())
                        .build()
                )
                .clubInfo(ClubInfo.builder()
                        .clubId(event.getClubId())
                        .clubName(event.getClubName())
                        .build()
                )
                .build();
    }

    public static EventDetailGetResponse withClub(Event event, int applicants, String bucketUrl) {
        return EventDetailGetResponse.builder()
                .id(event.getId())
                .category(event.getCategory().toString())
                .hasForm(event.hasForm())
                .eventInfo(
                        EventInfo.builder()
                                .title(event.getTitle())
                                .content(event.getContent())
                                .startDate(event.getStartDate())
                                .startTime(event.getStartTime())
                                .endDate(event.getEndDate())
                                .endTime(event.getEndTime())
                                .isEnded(event.isEventEnded())
                                .dues(event.getDues())
                                .location(event.getLocation())
                                .applicants(applicants)
                                .capacity(event.getCapacity())
                                .posterImageUrl(event.getPosterImageName() != null ? bucketUrl + event.getPosterImageName() : null)
                                .build()
                )
                .formInfo(
                        FormInfo.builder()
                                .openDate(event.getFormOpenDate())
                                .openTime(event.getFormOpenTime())
                                .closeDate(event.getFormCloseDate())
                                .closeTime(event.getFormCloseTime())
                                .isAbleToApply(event.isAbleToApply())
                                .build()
                )
                .clubInfo(ClubInfo.builder()
                        .clubId(event.getClubId())
                        .clubName(event.getClubName())
                        .build()
                )
                .build();
    }

    public static EventDetailGetResponse withPromotion(Event event, int applicants, String bucketUrl) {
        return EventDetailGetResponse.builder()
                .id(event.getId())
                .category(event.getCategory().toString())
                .hasForm(event.hasForm())
                .eventInfo(
                        EventInfo.builder()
                                .title(event.getTitle())
                                .content(event.getContent())
                                .startDate(event.getStartDate())
                                .startTime(event.getStartTime())
                                .isEnded(event.isEventEnded())
                                .applicants(applicants)
                                .capacity(event.getCapacity())
                                .posterImageUrl(event.getPosterImageName() != null ? bucketUrl + event.getPosterImageName() : null)
                                .activityArea(event.getActivityArea())
                                .build()
                )
                .formInfo(
                        FormInfo.builder()
                                .openDate(event.getFormOpenDate())
                                .openTime(event.getFormOpenTime())
                                .closeDate(event.getFormCloseDate())
                                .closeTime(event.getFormCloseTime())
                                .isAbleToApply(event.isAbleToApply())
                                .build()
                )
                .clubInfo(ClubInfo.builder()
                        .clubId(event.getClubId())
                        .clubName(event.getClubName())
                        .build()
                )
                .build();
    }

    public static EventDetailGetResponse withRecruitment(Event event, int applicants, String bucketUrl) {
        return EventDetailGetResponse.builder()
                .id(event.getId())
                .category(event.getCategory().toString())
                .hasForm(event.hasForm())
                .eventInfo(
                        EventInfo.builder()
                                .title(event.getTitle())
                                .content(event.getContent())
                                .startDate(event.getStartDate())
                                .startTime(event.getStartTime())
                                .isEnded(event.isEventEnded())
                                .location(event.getLocation())
                                .applicants(applicants)
                                .capacity(event.getCapacity())
                                .posterImageUrl(event.getPosterImageName() != null ? bucketUrl + event.getPosterImageName() : null)
                                .recruitmentTarget(event.getRecruitmentTarget())
                                .build()
                )
                .formInfo(
                        FormInfo.builder()
                                .openDate(event.getFormOpenDate())
                                .openTime(event.getFormOpenTime())
                                .closeDate(event.getFormCloseDate())
                                .closeTime(event.getFormCloseTime())
                                .isAbleToApply(event.isAbleToApply())
                                .build()
                )
                .clubInfo(ClubInfo.builder()
                        .clubId(event.getClubId())
                        .clubName(event.getClubName())
                        .build()
                )
                .build();
    }

}
