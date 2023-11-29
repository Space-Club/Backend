package com.spaceclub.event.controller.dto.updateRequest;

import com.spaceclub.event.domain.Event;
import com.spaceclub.event.domain.EventCategory;
import com.spaceclub.event.domain.EventInfo;
import com.spaceclub.event.domain.FormInfo;

import java.time.LocalDate;
import java.time.LocalTime;

public record RecruitmentEventUpdateRequest(
        Long eventId,
        EventInfoRequest eventInfo,
        FormInfoRequest formInfo
) {

    public Event toEntity(EventCategory category) {
        return Event.builder()
                .id(eventId)
                .category(category)
                .eventInfo(eventInfo.toEntity(formInfo))
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
            Integer recruitmentLimit
    ) {

        public EventInfo toEntity(FormInfoRequest formInfo) {
            return EventInfo.builder()
                    .title(title)
                    .content(content)
                    .startDateTime(formInfo.openDate.atTime(formInfo.openTime))
                    .endDateTime(formInfo.closeDate.atTime(formInfo.closeTime))
                    .activityArea(activityArea)
                    .recruitmentTarget(recruitmentTarget)
                    .recruitmentLimit(recruitmentLimit)
                    .build();
        }

    }

}
