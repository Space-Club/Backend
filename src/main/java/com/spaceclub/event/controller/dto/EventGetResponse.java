package com.spaceclub.event.controller.dto;

import com.spaceclub.event.domain.Event;

import java.time.LocalDate;
import java.time.LocalTime;

public record EventGetResponse(
        Long id,
        String title,
        String poster,
        LocalDate startDate,
        LocalTime startTime,
        String location,
        String clubName,
        String clubLogoImageUrl
) {

    public static EventGetResponse from(Event event) {
        return new EventGetResponse(
                event.getId(),
                event.getEventInfo().getTitle(),
                event.getEventInfo().getPoster(),
                event.getEventInfo().getStartDate().toLocalDate(),
                event.getEventInfo().getStartDate().toLocalTime(),
                event.getEventInfo().getLocation(),
                event.getClub().getName(),
                event.getClub().getLogoImageUrl()
        );
    }

}
