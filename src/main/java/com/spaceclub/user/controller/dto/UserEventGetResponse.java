package com.spaceclub.user.controller.dto;

import com.spaceclub.event.domain.ParticipationStatus;
import com.spaceclub.event.service.vo.EventPageInfo;

import java.time.LocalDate;

public record UserEventGetResponse(
        Long id,
        String title,
        String location,
        String clubName,
        String posterImageUrl,
        LocalDate startDate,
        ParticipationStatus participationStatus,
        Integer ticketCount
) {

    public static UserEventGetResponse from(EventPageInfo event, Integer ticketCount) {
        return new UserEventGetResponse(
                event.id(),
                event.title(),
                event.location(),
                event.clubName(),
                event.posterImageUrl(),
                event.startDate(),
                event.participationStatus(),
                ticketCount
        );
    }

}
