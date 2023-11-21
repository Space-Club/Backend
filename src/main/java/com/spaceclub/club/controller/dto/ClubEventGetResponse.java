package com.spaceclub.club.controller.dto;

import com.spaceclub.event.service.vo.EventGetInfo;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalTime;

@Builder
public record ClubEventGetResponse(
        Long id,
        String title,
        String posterImageUrl,
        LocalDate startDate,
        LocalTime startTime,
        String location,
        String clubName,
        String clubLogoImageUrl,
        String openStatus
) {

    public static ClubEventGetResponse from(EventGetInfo eventInfo) {
        return new ClubEventGetResponse(
                eventInfo.id(),
                eventInfo.title(),
                eventInfo.posterImageUrl(),
                eventInfo.startDate(),
                eventInfo.startTime(),
                eventInfo.location(),
                eventInfo.clubName(),
                eventInfo.clubLogoImageUrl(),
                eventInfo.category()
        );
    }

}
