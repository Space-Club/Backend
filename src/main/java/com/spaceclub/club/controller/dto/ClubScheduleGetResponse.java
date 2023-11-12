package com.spaceclub.club.controller.dto;

import com.spaceclub.event.domain.Event;

import java.time.LocalDateTime;
import java.util.List;

public record ClubScheduleGetResponse(
        List<ClubScheduleGetResponseInfo> schedules
) {

    public record ClubScheduleGetResponseInfo(
            String title,
            String content,
            LocalDateTime startDateTime
    ) {

        public ClubScheduleGetResponseInfo(Event event) {
            this(event.getTitle(),
                    event.getContent(),
                    LocalDateTime.of(event.getStartDate(), event.getStartTime()));
        }

    }

}
