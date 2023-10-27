package com.spaceclub.user.controller.dto;

import com.spaceclub.event.domain.Event;

public record UserEventGetResponse(Long id, String title, String location, String host) {

    public static UserEventGetResponse from(Event event) {
        return new UserEventGetResponse(
                event.getId(),
                event.getEventInfo().getTitle(),
                event.getEventInfo().getLocation(),
                event.getClubHost()
        );
    }

}
