package com.spaceclub.user.controller.dto;

import com.spaceclub.event.domain.Event;

import java.time.LocalDate;

public record UserEventGetResponse(
        Long id,
        String title,
        String location,
        String clubName,
        String posterImageUrl,
        LocalDate startDate,
        String status
) {

    public static UserEventGetResponse from(Event event, String eventStatus) {
        return new UserEventGetResponse(
                event.getId(),
                event.getTitle(),
                event.getLocation(),
                event.getClubName(),
                event.getPosterImageUrl(),
                event.getStartDate(),
                eventStatus
        );
    }

}
