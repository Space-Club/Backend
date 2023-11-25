package com.spaceclub.club.controller.dto;

import com.spaceclub.event.service.vo.EventGetInfo;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Builder
public record ClubEventGetResponse(Long id,
                                   EventInfoResponse eventInfo,
                                   ClubInfoResponse clubInfo) {

    public static ClubEventGetResponse from(EventGetInfo eventGetInfo) {
        return new ClubEventGetResponse(
                eventGetInfo.id(),
                new EventInfoResponse(
                        eventGetInfo.title(),
                        eventGetInfo.posterImageUrl(),
                        eventGetInfo.location(),
                        eventGetInfo.startDate(),
                        eventGetInfo.startTime(),
                        eventGetInfo.endDate(),
                        eventGetInfo.endTime(),
                        eventGetInfo.openStatus(),
                        isEventEnded(eventGetInfo)
                ),
                new ClubInfoResponse(
                        eventGetInfo.clubName(),
                        eventGetInfo.clubLogoImageUrl()
                )
        );
    }

    private static boolean isEventEnded(EventGetInfo eventGetInfo) {
        if (eventGetInfo.endDate() == null) {
            return LocalDateTime.now().isAfter(eventGetInfo.startDate().atTime(eventGetInfo.startTime()));
        }

        return LocalDateTime.now().isAfter(eventGetInfo.endDate().atTime(eventGetInfo.endTime()));
    }

    private record EventInfoResponse(
            String title,
            String posterImageUrl,
            String location,
            LocalDate startDate,
            LocalTime startTime,
            LocalDate endDate,
            LocalTime endTime,
            String openStatus,
            boolean isEnded
    ) {

    }

    private record ClubInfoResponse(
            String name,
            String logoImageUrl
    ) {

    }

}
