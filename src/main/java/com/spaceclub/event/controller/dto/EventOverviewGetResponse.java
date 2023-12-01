package com.spaceclub.event.controller.dto;

import com.spaceclub.event.domain.Event;

import java.time.LocalDate;
import java.time.LocalTime;

public record EventOverviewGetResponse(Long id,
                                       EventInfoResponse eventInfo,
                                       ClubInfoResponse clubInfo) {

    public static EventOverviewGetResponse from(Event event, String bucketUrl) {
        return new EventOverviewGetResponse(
                event.getId(),
                new EventInfoResponse(
                        event.getTitle(),
                        event.getPosterImageName() == null ? null : bucketUrl + event.getPosterImageName(),
                        event.getLocation(),
                        event.getStartDate(),
                        event.getStartTime(),
                        event.getEndDate(),
                        event.getEndTime(),
                        event.isEventEnded()
                ),
                new ClubInfoResponse(
                        event.getClubName(),
                        event.getClubLogoImageName() == null ? null : bucketUrl + event.getClubLogoImageName(),
                        event.getClubCoverImageName() == null ? null : bucketUrl + event.getClubLogoImageName()
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
            String logoImageUrl,
            String coverImageUrl
    ) {

    }

}
