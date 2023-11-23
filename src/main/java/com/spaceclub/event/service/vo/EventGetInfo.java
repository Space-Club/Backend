package com.spaceclub.event.service.vo;

import com.spaceclub.event.domain.Event;
import com.spaceclub.event.domain.EventCategory;

import java.time.LocalDate;
import java.time.LocalTime;

public record EventGetInfo(
        Long id,
        String title,
        String posterImageUrl,
        LocalDate startDate,
        LocalTime startTime,
        String location,
        String clubName,
        String clubLogoImageUrl,
        String openStatus,
        LocalDate formOpenDate,
        LocalTime formOpenTime,
        LocalDate formCloseDate,
        LocalTime formCloseTime
) {

    public static EventGetInfo from(Event event) {
        return new EventGetInfo(
                event.getId(),
                event.getTitle(),
                event.getPosterImageUrl(),
                event.getStartDate(),
                event.getStartTime(),
                event.getLocation(),
                event.getClubName(),
                event.getClubLogoImageUrl(),
                event.getCategory().equals(EventCategory.CLUB) ? "CLUB" : "ALL",
                event.getFormOpenDate(),
                event.getFormOpenTime(),
                event.getFormCloseDate(),
                event.getFormCloseTime()
        );
    }

}
