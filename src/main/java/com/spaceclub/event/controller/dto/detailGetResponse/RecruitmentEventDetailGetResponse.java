package com.spaceclub.event.controller.dto.detailGetResponse;

import com.spaceclub.event.domain.Event;
import lombok.Builder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public record RecruitmentEventDetailGetResponse(
        Long id,
        String title,
        String posterImageUrl,
        String recruitmentTarget,
        Integer capacity,
        String activityArea,
        String clubName,
        String clubLogoImageUrl,
        LocalDateTime formOpenDateTime,
        LocalDateTime formCloseDateTime,
        Boolean isBookmarked,
        String eventCategory
) {

    @Builder
    public RecruitmentEventDetailGetResponse {
    }

    public static RecruitmentEventDetailGetResponse from(Event event, Boolean isBookmarked) {
        return RecruitmentEventDetailGetResponse.builder()
                .id(event.getId())
                .title(event.getTitle())
                .posterImageUrl(event.getPosterImageUrl())
                .recruitmentTarget(event.getRecruitmentTarget())
                .capacity(event.getCapacity())
                .activityArea(event.getActivityArea())
                .clubName(event.getClubName())
                .clubLogoImageUrl(event.getClubLogoImageUrl())
                .formOpenDateTime(event.getFormOpenDateTime())
                .formCloseDateTime(event.getFormCloseDateTime())
                .isBookmarked(isBookmarked)
                .eventCategory(event.getCategory().toString())
                .build();
    }

}
