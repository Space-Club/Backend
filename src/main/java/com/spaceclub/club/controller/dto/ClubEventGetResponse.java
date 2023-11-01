package com.spaceclub.club.controller.dto;

import com.spaceclub.event.domain.Category;
import com.spaceclub.event.domain.Event;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalTime;


@Builder
public record ClubEventGetResponse(
        Long id,
        String title,
        String poster,
        LocalDate startDate,
        LocalTime startTime,
        String location,
        String clubName,
        String clubImage,
        String openStatus
) {

    public static ClubEventGetResponse from(Event event) {
        return new ClubEventGetResponse(
                event.getId(),
                event.getTitle(),
                event.getPoster(),
                event.getStartDate(),
                event.getStartTime(),
                event.getLocation(),
                event.getClubName(),
                event.getClubImage(),
                event.getCategory().equals(Category.CLUB) ? "CLUB" : "ALL"
        );
    }

}
