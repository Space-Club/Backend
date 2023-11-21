package com.spaceclub.club.controller.dto;

import com.spaceclub.event.service.vo.SchedulesGetInfo;

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
            String manager
    ) {

        public static ClubScheduleGetResponseInfo from(SchedulesGetInfo vo) {
            return new ClubScheduleGetResponseInfo(
                    vo.eventId(),
                    vo.title(),
                    vo.startDateTime(),
                    vo.endDateTime(),
                    vo.manager()
            );
        }

    }

}
