package com.spaceclub.user.controller.dto;

import com.spaceclub.event.domain.Event;

public record EventResponse(Long id, String title, String location, String host) {

    public static EventResponse from(Event event) {
        return new EventResponse(
                event.getId(),
                event.getEventInfo().getTitle(),
                event.getEventInfo().getLocation(),
                event.getClubHost()
        );
    }

}
