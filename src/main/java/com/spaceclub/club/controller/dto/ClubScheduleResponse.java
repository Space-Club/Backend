package com.spaceclub.club.controller.dto;

import com.spaceclub.event.domain.Event;

import java.util.List;

public record ClubScheduleResponse(
        List<Event> schedules
) {

}
