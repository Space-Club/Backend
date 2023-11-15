package com.spaceclub.event.controller.dto.updateRequest;

import com.spaceclub.event.domain.Event;
import com.spaceclub.event.domain.EventCategory;
import com.spaceclub.event.domain.EventInfo;
import com.spaceclub.event.domain.FormInfo;

import java.time.LocalDate;
import java.time.LocalTime;

public record PromotionEventUpdateRequest(
        Long eventId,
        EventInfoRequest eventInfo,
        FormInfoRequest formInfo
) {

    public Event toEntity(EventCategory category, String posterImageUrl) {
        return Event.builder()
                .id(eventId)
                .category(category)
                .eventInfo(eventInfo.toEntity(posterImageUrl))
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
                    .formOpenDate(openDate.atTime(openTime))
                    .formCloseDate(closeDate.atTime(closeTime))
                    .build();
        }

    }

    public record EventInfoRequest(
            String title,
            String content,
            LocalDate startDate,
            LocalTime startTime,
            String location,
            int capacity
    ) {

        public EventInfo toEntity(String posterImageUrl) {
            return EventInfo.builder()
                    .title(title)
                    .content(content)
                    .startDate(startDate.atTime(startTime))
                    .location(location)
                    .capacity(capacity)
                    .posterImageUrl(posterImageUrl)
                    .build();
        }

    }

}
