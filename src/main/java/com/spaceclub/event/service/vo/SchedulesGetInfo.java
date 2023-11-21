package com.spaceclub.event.service.vo;

import com.spaceclub.event.domain.Event;

import java.time.LocalDateTime;

public record SchedulesGetInfo(
        Long eventId,
        String title,
        LocalDateTime startDateTime,
        LocalDateTime endDateTime,
        String manager
) {

    public static SchedulesGetInfo from(Event event) {
        return new SchedulesGetInfo(
                event.getId(),
                event.getTitle(),
                LocalDateTime.of(event.getStartDate(), event.getStartTime()),
                LocalDateTime.of(event.getEndDate(), event.getEndTime()),
                event.getManagerName()
        );
    }

}
