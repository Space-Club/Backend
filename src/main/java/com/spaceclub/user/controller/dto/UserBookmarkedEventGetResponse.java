package com.spaceclub.user.controller.dto;

import com.spaceclub.event.service.vo.EventGetInfo;

import java.time.LocalDate;

public record UserBookmarkedEventGetResponse(
        Long id,
        String title,
        String location,
        String clubName,
        String posterImageUrl,
        LocalDate startDate,
        boolean bookmark
) {

    public static UserBookmarkedEventGetResponse from(EventGetInfo event) {
        return new UserBookmarkedEventGetResponse(
                event.id(),
                event.title(),
                event.location(),
                event.clubName(),
                event.posterImageUrl(),
                event.startDate(),
                true
        );
    }

}
