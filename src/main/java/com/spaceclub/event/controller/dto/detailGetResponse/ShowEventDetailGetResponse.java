package com.spaceclub.event.controller.dto.detailGetResponse;

import com.spaceclub.event.domain.Event;
import com.spaceclub.event.domain.EventCategory;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record ShowEventDetailGetResponse(
        Long id,
        String title,
        String posterImageUrl,
        LocalDate startDate,
        LocalTime startTime,
        String location,
        Integer dues,
        String clubName,
        String clubLogoImageUrl,
        LocalDateTime formOpenDateTime,
        LocalDateTime formCloseDateTime,
        Boolean isBookmarked,
        Integer applicants,
        Integer capacity,
        String eventCategory

) {

    @Builder
    public ShowEventDetailGetResponse {
    }

    public static ShowEventDetailGetResponse from(Event event, Boolean isBookmarked, Integer applicants) {
        return ShowEventDetailGetResponse.builder()
                .id(event.getId())
                .title(event.getTitle())
                .posterImageUrl(event.getPosterImageUrl())
                .startDate(event.getStartDate())
                .startTime(event.getStartTime())
                .location(event.getLocation())
                .dues(event.getDues())
                .clubName(event.getClubName())
                .clubLogoImageUrl(event.getClubLogoImageUrl())
                .formOpenDateTime(event.getFormOpenDateTime())
                .formCloseDateTime(event.getFormCloseDateTime())
                .isBookmarked(isBookmarked)
                .applicants(applicants)
                .capacity(event.getCapacity())
                .eventCategory(event.getCategory().toString())
                .build();
    }

}
