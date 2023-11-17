package com.spaceclub.event.controller.dto;

import com.fasterxml.jackson.annotation.JsonFilter;
import com.spaceclub.event.domain.Event;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalTime;

@JsonFilter("EventDetailFilter")
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

    public static EventDetailGetResponse from(Event event, Boolean isBookmarked, Boolean isManager, Boolean hasForm) {
        return EventDetailGetResponse.builder()
                .id(event.getId())
                .title(event.getTitle())
                .content(event.getContent())
                .posterImageUrl(event.getPosterImageUrl())
                .startDate(event.getStartDate())
                .startTime(event.getStartTime())
                .endDate(event.getEndDate())
                .endTime(event.getEndTime())
                .location(event.getLocation())
                .dues(event.getDues())
                .cost(event.getCost())
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
                .recruitmentTarget(event.getRecruitmentTarget())
                .eventCategory(event.getCategory().toString())
                .isManager(isManager)
                .hasForm(hasForm)
                .maxTicketCount(event.getMaxTicketCount())
                .build();
    }

}
