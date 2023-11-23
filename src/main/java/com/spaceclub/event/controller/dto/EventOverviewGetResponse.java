package com.spaceclub.event.controller.dto;

import com.spaceclub.event.domain.Event;

import java.time.LocalDate;
import java.time.LocalTime;

public record EventOverviewGetResponse(Long id,
                                       EventInfoResponse eventInfo,
                                       FormInfoResponse formInfo,
                                       ClubInfoResponse clubInfo) {

    public static EventOverviewGetResponse from(Event event) {
        return new EventOverviewGetResponse(
                event.getId(),
                new EventInfoResponse(
                        event.getTitle(),
                        event.getPosterImageUrl(),
                        event.getLocation(),
                        event.getStartDate(),
                        event.getStartTime()
                ),
                new FormInfoResponse(
                        event.getFormOpenDate(),
                        event.getFormOpenTime(),
                        event.getFormCloseDate(),
                        event.getFormCloseTime()
                ),
                new ClubInfoResponse(
                        event.getClubName(),
                        event.getClubLogoImageUrl()
                )
        );
    }

    private record EventInfoResponse(
            String title,
            String posterImageUrl,
            String location,
            LocalDate startDate,
            LocalTime startTime
    ) {

    }

    private record FormInfoResponse(
            LocalDate openDate,
            LocalTime openTime,
            LocalDate closeDate,
            LocalTime closeTime
    ) {

    }

    private record ClubInfoResponse(
            String name,
            String logoImageUrl
    ) {

    }

}
