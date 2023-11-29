package com.spaceclub.event.controller.dto;

import com.spaceclub.event.domain.Event;

import java.time.LocalDateTime;

public record EventBannerResponse(
        ClubInfoResponse clubInfo,
        EventInfoResponse eventInfo
) {

    public static EventBannerResponse from(Event event) {
        ClubInfoResponse clubInfoResponse = new ClubInfoResponse(
                event.getClubName(),
                event.getClubCoverImageName()
        );
        EventInfoResponse eventInfoResponse = new EventInfoResponse(
                event.getId(),
                event.getTitle(),
                event.getFormCloseDateTime(),
                event.getCategory().name()
        );

        return new EventBannerResponse(clubInfoResponse, eventInfoResponse);
    }

}

record EventInfoResponse(
        Long eventId,
        String title,
        LocalDateTime formCloseDateTime,
        String eventCategory
) {

}

record ClubInfoResponse(
        String coverImageUrl,
        String name
) {

}
