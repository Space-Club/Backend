package com.spaceclub.event.controller.dto;

import com.spaceclub.event.domain.Event;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record EventDetailGetResponse(
        Long id,
        String title,
        String posterImageUrl,
        LocalDate startDate,
        LocalTime startTime,
        String location,
        String clubName,
        String clubLogoImageUrl,
        LocalDateTime formOpenDateTime,
        LocalDateTime formCloseDateTime
) {

    @Builder
    public EventDetailGetResponse {
    }

    public static EventDetailGetResponse from(Event event) {
        return EventDetailGetResponse.builder()
                .id(event.getId())
                .title(event.getTitle())
                .posterImageUrl(event.getPosterImageUrl())
                .startDate(event.getStartDate())
                .startTime(event.getStartTime())
                .location(event.getLocation())
                .clubName(event.getClubName())
                .clubLogoImageUrl(event.getClubLogoImageUrl())
                .formOpenDateTime(event.getFormOpenDateTime())
                .formCloseDateTime(event.getFormCloseDateTime())
                .build();

    }

}
