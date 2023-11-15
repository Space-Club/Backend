package com.spaceclub.club.controller.dto;

import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

public record ClubScheduleGetResponse(
        List<ClubScheduleGetResponseInfo> schedules
) {

    public record ClubScheduleGetResponseInfo(
            Long eventId,
            String title,
            LocalDateTime startDateTime,
            LocalDateTime endDateTime,
            String manager,
            String profileImageUrl
    ) {

        @Builder
        public ClubScheduleGetResponseInfo {
        }

    }

}
