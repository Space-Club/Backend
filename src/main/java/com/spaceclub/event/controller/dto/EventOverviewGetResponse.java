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

    public record EventInfoResponse(
            String title,
            String posterImageUrl,
            String location,
            LocalDate startDate,
            LocalTime startTime
    ) {

    }

    public record FormInfoResponse(
            LocalDate startDate,
            LocalTime startTime,
            LocalDate endDate,
            LocalTime endTime
    ) {

    }

    public record ClubInfoResponse(
            String name,
            String logoImageUrl
    ) {

    }

}
