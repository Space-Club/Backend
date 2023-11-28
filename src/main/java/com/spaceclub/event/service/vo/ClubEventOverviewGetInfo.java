package com.spaceclub.event.service.vo;

import com.spaceclub.event.domain.Event;
import com.spaceclub.event.domain.EventCategory;
import com.spaceclub.user.service.vo.UserProfile;

import java.time.LocalDate;
import java.time.LocalTime;

public record ClubEventOverviewGetInfo(
        Long id,
        String title,
        String posterImageUrl,
        LocalDate startDate,
        LocalTime startTime,
        LocalDate endDate,
        LocalTime endTime,
        String location,
        String openStatus,
        String clubName,
        String clubLogoImageUrl,
        String managerName,
        String managerProfileImageUrl
) {

    public static ClubEventOverviewGetInfo from(Event event, UserProfile userProfile, String bucketUrl) {
        return new ClubEventOverviewGetInfo(
                event.getId(),
                event.getTitle(),
                event.getPosterImageName() != null ? bucketUrl + event.getPosterImageName() : null,
                event.getStartDate(),
                event.getStartTime(),
                event.getEndDate(),
                event.getEndTime(),
                event.getLocation(),
                event.getCategory().equals(EventCategory.CLUB) ? "CLUB" : "ALL",
                event.getClubName(),
                bucketUrl + event.getClubLogoImageName(),
                userProfile.username(),
                userProfile.profileImageUrl()
        );
    }

}
