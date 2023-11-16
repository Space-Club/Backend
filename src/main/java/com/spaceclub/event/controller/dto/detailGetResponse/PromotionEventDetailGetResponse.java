package com.spaceclub.event.controller.dto.detailGetResponse;

import com.spaceclub.event.domain.Event;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record PromotionEventDetailGetResponse(
        Long id,
        String title,
        String posterImageUrl,
        LocalDate startDate,
        LocalTime startTime,
        String location,
        LocalDateTime formOpenDateTime,
        LocalDateTime formCloseDateTime,
        String clubName,
        String clubLogoImageUrl,
        Boolean isBookmarked,
        Integer applicants,
        Integer capacity,
        String eventCategory
) {

    @Builder
    public PromotionEventDetailGetResponse {
    }

    public static PromotionEventDetailGetResponse from(Event event, Boolean isBookmarked, Integer applicants) {
        return PromotionEventDetailGetResponse.builder()
                .id(event.getId())
                .title(event.getTitle())
                .posterImageUrl(event.getPosterImageUrl())
                .startDate(event.getStartDate())
                .startTime(event.getStartTime())
                .location(event.getLocation())
                .formOpenDateTime(event.getFormOpenDateTime())
                .formCloseDateTime(event.getFormCloseDateTime())
                .clubName(event.getClubName())
                .clubLogoImageUrl(event.getClubLogoImageUrl())
                .isBookmarked(isBookmarked)
                .applicants(applicants)
                .capacity(event.getCapacity())
                .eventCategory(event.getCategory().toString())
                .build();
    }

}
