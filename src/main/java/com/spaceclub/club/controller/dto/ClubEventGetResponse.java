package com.spaceclub.club.controller.dto;

import com.spaceclub.event.domain.Category;
import com.spaceclub.event.domain.Event;

import java.time.LocalDate;
import java.time.LocalTime;

public record ClubEventGetResponse(
        Long id,
        String title,
        String poster,
        LocalDate startDate,
        LocalTime startTime,
        String location,
        String clubName,
        String clubImage,
        String clubStatus
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
                Category.CLUB.toString() // 임시로 넣음
        );
    }

}
