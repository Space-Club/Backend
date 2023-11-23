package com.spaceclub.club.controller.dto;

import com.spaceclub.event.service.vo.EventGetInfo;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalTime;

@Builder
public record ClubEventGetResponse(Long id,
                                   EventInfoResponse eventInfo,
                                   FormInfoResponse formInfo,
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
                        eventGetInfo.openStatus()
                ),
                new FormInfoResponse(
                        eventGetInfo.formOpenDate(),
                        eventGetInfo.formOpenTime(),
                        eventGetInfo.formCloseDate(),
                        eventGetInfo.formCloseTime()
                ),
                new ClubInfoResponse(
                        eventGetInfo.clubName(),
                        eventGetInfo.clubLogoImageUrl()
                )
        );
    }

    private record EventInfoResponse(
            String title,
            String posterImageUrl,
            String location,
            LocalDate startDate,
            LocalTime startTime,
            String openStatus
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
