package com.spaceclub.event.controller.dto.createRequest;

import com.spaceclub.event.domain.Event;
import com.spaceclub.event.domain.EventCategory;
import com.spaceclub.event.domain.EventInfo;
import com.spaceclub.event.domain.FormInfo;

import java.time.LocalDate;
import java.time.LocalTime;

public record RecruitmentEventCreateRequest(
        Long clubId,
        EventInfoRequest eventInfo,
        FormInfoRequest formInfo
) {

    public Event toEntity(EventCategory category) {
        return Event.builder()
                .category(category)
                .eventInfo(eventInfo.toEntity())
                .formInfo(formInfo.toEntity())
                .build();
    }

    public record FormInfoRequest(
            LocalDate openDate,
            LocalTime openTime,
            LocalDate closeDate,
            LocalTime closeTime
    ) {

        public FormInfo toEntity() {
            return FormInfo.builder()
                    .formOpenDateTime(openDate.atTime(openTime))
                    .formCloseDateTime(closeDate.atTime(closeTime))
                    .build();
        }

    }

    public record EventInfoRequest(
            String title,
            String content,
            String activityArea,
            String recruitmentTarget,
            int capacity
    ) {

        public EventInfo toEntity() {
            return EventInfo.builder()
                    .title(title)
                    .content(content)
                    .activityArea(activityArea)
                    .recruitmentTarget(recruitmentTarget)
                    .capacity(capacity)
                    .build();
        }

    }

}
