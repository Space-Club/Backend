package com.spaceclub.event.service.vo;

import com.spaceclub.event.domain.Event;

import java.time.LocalDate;

public record UserBookmarkedEventGetInfo(
        Long id,
        String title,
        String location,
        String clubName,
        String posterImageUrl,
        LocalDate startDate
) {

    public static UserBookmarkedEventGetInfo from(Event event, String bucketUrl) {
        return new UserBookmarkedEventGetInfo(
                event.getId(),
                event.getTitle(),
                event.getLocation(),
                event.getClubName(),
                event.getPosterImageName() != null ? bucketUrl + event.getPosterImageName() : null,
                event.getStartDate()
        );
    }

}
