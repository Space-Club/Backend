package com.spaceclub.user.controller.dto;

import com.spaceclub.event.domain.Event;

public record UserEventGetResponse(
        Long id,
        String title,
        String location,
        String clubName,
        String posterImageUrl,
        String startDate,
        String status
) {

    public static UserEventGetResponse from(Event event, String eventStatus) {
        return new UserEventGetResponse(
                event.getId(),
                event.getEventInfo().getTitle(),
                event.getEventInfo().getLocation(),
                event.getClub().getName(),
                event.getEventInfo().getPosterImageUrl(),
                event.getEventInfo().getStartDate().toLocalDate().toString(),
                eventStatus
        );
    }

}
