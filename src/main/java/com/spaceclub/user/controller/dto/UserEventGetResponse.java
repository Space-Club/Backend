package com.spaceclub.user.controller.dto;

import com.spaceclub.event.service.vo.EventPageInfo;

import java.time.LocalDate;

public record UserEventGetResponse(
        Long id,
        String title,
        String location,
        String clubName,
        String posterImageUrl,
        LocalDate startDate,
        String status
) {

    public static UserEventGetResponse from(EventPageInfo event) {
        return new UserEventGetResponse(
                event.id(),
                event.title(),
                event.location(),
                event.clubName(),
                event.posterImageUrl(),
                event.startDate(),
                event.status()
        );
    }

}
