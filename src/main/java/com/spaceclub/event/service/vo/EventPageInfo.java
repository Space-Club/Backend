package com.spaceclub.event.service.vo;

import com.spaceclub.event.domain.Event;
import com.spaceclub.event.domain.EventUser;
import com.spaceclub.event.domain.ParticipationStatus;

import java.time.LocalDate;

public record EventPageInfo(
        Long id,
        String title,
        String location,
        String clubName,
        String posterImageUrl,
        LocalDate startDate,
        ParticipationStatus participationStatus
) {

    public static EventPageInfo from(Event event, EventUser eventUser) {
        return new EventPageInfo(
                event.getId(),
                event.getTitle(),
                event.getLocation(),
                event.getClubName(),
                event.getPosterImageUrl(),
                event.getStartDate(),
                eventUser.getStatus()
        );

    }

}
