package com.spaceclub.event.controller.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.spaceclub.event.domain.Event;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalTime;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record EventDetailGetResponse(
        Long id,
        String title,
        String content,
        String posterImageUrl,
        String recruitmentTarget,
        LocalDate startDate,
        LocalTime startTime,
        LocalDate endDate,
        LocalTime endTime,
        String location,
        Integer dues,
        Integer cost,
        String activityArea,
        LocalDate formOpenDate,
        LocalTime formOpenTime,
        LocalDate formCloseDate,
        LocalTime formCloseTime,
        String clubName,
        String clubLogoImageUrl,
        Boolean isBookmarked,
        Integer applicants,
        Integer capacity,
        String eventCategory,
        Boolean isManager,
        Boolean hasForm,
        Integer maxTicketCount
) {

    @Builder
    public EventDetailGetResponse {
    }

    public static EventDetailGetResponse withShow(Event event, Boolean isBookmarked, Boolean isManager, Boolean hasForm) {
        return EventDetailGetResponse.builder()
                .id(event.getId())
                .title(event.getTitle())
                .content(event.getContent())
                .posterImageUrl(event.getPosterImageUrl())
                .startDate(event.getStartDate())
                .startTime(event.getStartTime())
                .location(event.getLocation())
                .cost(event.getCost())
                .clubName(event.getClubName())
                .clubLogoImageUrl(event.getClubLogoImageUrl())
                .formOpenDate(event.getFormOpenDate())
                .formOpenTime(event.getFormOpenTime())
                .formCloseDate(event.getFormCloseDate())
                .formCloseTime(event.getFormCloseTime())
                .isBookmarked(isBookmarked)
                .applicants(10)
                .capacity(10)
                .eventCategory(event.getCategory().toString())
                .isManager(isManager)
                .hasForm(hasForm)
                .maxTicketCount(event.getMaxTicketCount())
                .build();
    }

    public static EventDetailGetResponse withClub(Event event, Boolean isBookmarked, Boolean isManager, Boolean hasForm) {
        return EventDetailGetResponse.builder()
                .id(event.getId())
                .title(event.getTitle())
                .content(event.getContent())
                .posterImageUrl(event.getPosterImageUrl())
                .startDate(event.getStartDate())
                .startTime(event.getStartTime())
                .endDate(event.getEndDate() == null ? LocalDate.EPOCH : event.getEndDate())
                .endTime(event.getEndTime() == null ? LocalTime.MIN : event.getEndTime())
                .location(event.getLocation())
                .dues(event.getDues())
                .clubName(event.getClubName())
                .clubLogoImageUrl(event.getClubLogoImageUrl())
                .formOpenDate(event.getFormOpenDate())
                .formOpenTime(event.getFormOpenTime())
                .formCloseDate(event.getFormCloseDate())
                .formCloseTime(event.getFormCloseTime())
                .isBookmarked(isBookmarked)
                .applicants(10)
                .capacity(10)
                .eventCategory(event.getCategory().toString())
                .isManager(isManager)
                .hasForm(hasForm)
                .build();
    }

    public static EventDetailGetResponse withPromotion(Event event, Boolean isBookmarked, Boolean isManager, Boolean hasForm) {
        return EventDetailGetResponse.builder()
                .id(event.getId())
                .title(event.getTitle())
                .content(event.getContent())
                .posterImageUrl(event.getPosterImageUrl())
                .startDate(event.getStartDate())
                .startTime(event.getStartTime())
                .activityArea(event.getActivityArea())
                .clubName(event.getClubName())
                .clubLogoImageUrl(event.getClubLogoImageUrl())
                .formOpenDate(event.getFormOpenDate())
                .formOpenTime(event.getFormOpenTime())
                .formCloseDate(event.getFormCloseDate())
                .formCloseTime(event.getFormCloseTime())
                .isBookmarked(isBookmarked)
                .applicants(10)
                .capacity(10)
                .eventCategory(event.getCategory().toString())
                .isManager(isManager)
                .hasForm(hasForm)
                .build();
    }

    public static EventDetailGetResponse withRecruitment(Event event, Boolean isBookmarked, Boolean isManager, Boolean hasForm) {
        return EventDetailGetResponse.builder()
                .id(event.getId())
                .title(event.getTitle())
                .content(event.getContent())
                .posterImageUrl(event.getPosterImageUrl())
                .startDate(event.getStartDate())
                .startTime(event.getStartTime())
                .location(event.getLocation())
                .clubName(event.getClubName())
                .clubLogoImageUrl(event.getClubLogoImageUrl())
                .formOpenDate(event.getFormOpenDate())
                .formOpenTime(event.getFormOpenTime())
                .formCloseDate(event.getFormCloseDate())
                .formCloseTime(event.getFormCloseTime())
                .isBookmarked(isBookmarked)
                .applicants(10)
                .capacity(10)
                .recruitmentTarget(event.getRecruitmentTarget())
                .eventCategory(event.getCategory().toString())
                .isManager(isManager)
                .hasForm(hasForm)
                .maxTicketCount(event.getMaxTicketCount())
                .build();
    }

}
