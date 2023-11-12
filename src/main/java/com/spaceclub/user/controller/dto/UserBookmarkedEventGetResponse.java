package com.spaceclub.user.controller.dto;

import com.spaceclub.event.domain.Event;

import java.time.LocalDate;

public record UserBookmarkedEventGetResponse(
        Long id,
        String title,
        String location,
        String clubName,
        String posterImageUrl,
        LocalDate startDate
) {

        public static UserBookmarkedEventGetResponse from(Event event) {
            return new UserBookmarkedEventGetResponse(
                    event.getId(),
                    event.getTitle(),
                    event.getLocation(),
                    event.getClubName(),
                    event.getPosterImageUrl(),
                    event.getStartDate()
            );
        }

}
