package com.spaceclub.event.controller.dto;

import com.spaceclub.event.domain.Event;

import java.time.LocalDate;
import java.time.LocalTime;

public record EventSearchOverviewGetResponse(Long id,
                                             EventInfoResponse eventInfo,
                                             ClubInfoResponse clubInfo) {

    public static EventSearchOverviewGetResponse from(Event event, String bucketUrl) {
        return new EventSearchOverviewGetResponse(
                event.getId(),
                new EventInfoResponse(
                        event.getTitle(),
                        event.getPosterImageName() != null ? bucketUrl + event.getPosterImageName() : null,
                        event.getLocation(),
                        event.getStartDate(),
                        event.getStartTime(),
                        event.getEndDate(),
                        event.getEndTime(),
                        event.isEventEnded()
                ),
                new ClubInfoResponse(
                        event.getClubName(),
                        bucketUrl + event.getClubLogoImageName()
                )
        );
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
