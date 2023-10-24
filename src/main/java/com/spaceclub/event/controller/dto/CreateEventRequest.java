package com.spaceclub.event.controller.dto;

import com.spaceclub.event.domain.Event;

public record CreateEventRequest(Long id) {

    public Event toEntity() {
        return new Event();
    }

}
