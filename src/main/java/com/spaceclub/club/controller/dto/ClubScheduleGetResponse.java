package com.spaceclub.club.controller.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ClubScheduleGetResponse(
        List<ClubScheduleResponseInfo> schedules
) {

    public record ClubScheduleResponseInfo(
            String title,
            String content,
            LocalDateTime startDateTime
    ) {

    }

}
