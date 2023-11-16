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
        String location,
        String clubName,
        String clubLogoImageUrl,
        LocalDateTime formOpenDateTime,
        LocalDateTime formCloseDateTime,
        String eventCategory
) {

    @Builder
    public RecruitmentEventDetailGetResponse {
    }

    public static RecruitmentEventDetailGetResponse from(Event event) {
        return RecruitmentEventDetailGetResponse.builder()
                .id(event.getId())
                .title(event.getTitle())
                .posterImageUrl(event.getPosterImageUrl())
                .recruitmentTarget(event.getRecruitmentTarget())
                .capacity(event.getCapacity())
                .location(event.getLocation())
                .clubName(event.getClubName())
                .clubLogoImageUrl(event.getClubLogoImageUrl())
                .formOpenDateTime(event.getFormOpenDateTime())
                .formCloseDateTime(event.getFormCloseDateTime())
                .eventCategory(event.getCategory().toString())
                .build();
    }

}
