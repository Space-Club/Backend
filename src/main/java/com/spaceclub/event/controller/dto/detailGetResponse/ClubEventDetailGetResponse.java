package com.spaceclub.event.controller.dto.detailGetResponse;

import com.spaceclub.event.domain.Event;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record ClubEventDetailGetResponse(
        Long id,
        String title,
        String posterImageUrl,
        LocalDate startDate,
        LocalTime startTime,
        String location,
        LocalDateTime formOpenDateTime,
        LocalDateTime formCloseDateTime,
        String clubName,
        String clubLogoImageUrl,
        String eventCategory
) {

    @Builder
    public ClubEventDetailGetResponse {
    }

    public static ClubEventDetailGetResponse from(Event event) {
        return ClubEventDetailGetResponse.builder()
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
