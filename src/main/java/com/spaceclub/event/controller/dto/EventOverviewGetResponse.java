package com.spaceclub.event.controller.dto;

import com.spaceclub.event.domain.Event;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record EventOverviewGetResponse(Long id,
                                       EventInfoResponse eventInfo,
                                       ClubInfoResponse clubInfo) {

    public static EventOverviewGetResponse from(Event event) {
        return new EventOverviewGetResponse(
                event.getId(),
                new EventInfoResponse(
                        event.getTitle(),
                        event.getPosterImageUrl(),
                        event.getLocation(),
                        event.getStartDate(),
                        event.getStartTime(),
                        event.getEndDate(),
                        event.getEndTime(),
                        isEventEnded(event)
                ),
                new ClubInfoResponse(
                        event.getClubName(),
                        event.getClubLogoImageUrl()
                )
        );
    }

    private static boolean isEventEnded(Event event) {
        if (event.getEndDate() == null) {
            return LocalDateTime.now().isAfter(event.getStartDate().atTime(event.getStartTime()));
        }

        return LocalDateTime.now().isAfter(event.getEndDate().atTime(event.getEndTime()));
    }

    private record EventInfoResponse(
            String title,
            String posterImageUrl,
            String location,
            LocalDate startDate,
            LocalTime startTime,
            LocalDate endDate,
            LocalTime endTime,
            boolean isEnded
    ) {

    }

    private record ClubInfoResponse(
            String name,
            String logoImageUrl
    ) {

    }

}
