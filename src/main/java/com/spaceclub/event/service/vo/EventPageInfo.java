package com.spaceclub.event.service.vo;

import com.spaceclub.event.domain.Event;
import com.spaceclub.event.domain.EventUser;

import java.time.LocalDate;

public record EventPageInfo(
        Long id,
        String title,
        String location,
        String clubName,
        String posterImageUrl,
        LocalDate startDate,
        String status
) {

    public static EventPageInfo from(Event event, EventUser eventUser) {
        return new EventPageInfo(
                event.getId(),
                event.getTitle(),
                event.getLocation(),
                event.getClubName(),
                event.getPosterImageUrl(),
                event.getStartDate(),
                eventUser.getStatus().name()
        );

    }

}
