package com.spaceclub.event.controller.dto;

import com.spaceclub.event.domain.Event;

import java.time.LocalDate;
import java.time.LocalTime;

public record EventGetResponse(
        Long id,
        String title,
        String posterImageUrl,
        LocalDate startDate,
        LocalTime startTime,
        String location,
        String clubName,
        String clubLogoImageUrl
) {

    public static EventGetResponse from(Event event) {
        return new EventGetResponse(
                event.getId(),
                event.getTitle(),
                event.getPosterImageUrl(),
                event.getStartDate(),
                event.getStartTime(),
                event.getLocation(),
                event.getClubName(),
                event.getClubLogoImageUrl()
        );
    }

}
