package com.spaceclub.event.domain;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Lob;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static com.spaceclub.event.EventExceptionMessage.INVALID_EVENT_CAPACITY;
import static com.spaceclub.event.EventExceptionMessage.INVALID_EVENT_CONTENT;
import static com.spaceclub.event.EventExceptionMessage.INVALID_EVENT_LOCATION;
import static com.spaceclub.event.EventExceptionMessage.INVALID_EVENT_TITLE;

@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventInfo {

    private static final int TITLE_MAX_LENGTH = 30;

    private static final int CONTENT_MAX_LENGTH = 200;

    private static final int LOCATION_MAX_LENGTH = 30;

    private static final int CAPACITY_MIN_LENGTH = 1;

    private static final int CAPACITY_MAX_LENGTH = 999;

    @Getter
    private String title;

    @Getter
    private String content;

    @Getter
    private LocalDateTime startDateTime;

    @Getter
    private LocalDateTime endDateTime;

    @Getter
    private Integer dues;

    @Getter
    private String location;

    @Getter
    private Integer capacity;

    @Lob
    @Getter
    private String posterImageName;

    @Getter
    private String activityArea;

    @Getter
    private String recruitmentTarget;

    @Getter
    private Integer recruitmentLimit;

    @Builder
    private EventInfo(
            String title,
            String content,
            LocalDateTime startDateTime,
            String location,
            Integer capacity,
            String posterImageName,
            String activityArea,
            String recruitmentTarget,
            Integer recruitmentLimit,
            LocalDateTime endDateTime,
            Integer dues
    ) {
        validate(title, content, location, capacity);
        this.title = title;
        this.content = content;
        this.startDateTime = startDateTime;
        this.location = location;
        this.capacity = capacity;
        this.posterImageName = posterImageName;
        this.activityArea = activityArea;
        this.recruitmentTarget = recruitmentTarget;
        this.recruitmentLimit = recruitmentLimit;
        this.endDateTime = endDateTime;
        this.dues = dues;
    }

    public EventInfo registerPosterImage(String posterImageName) {
        return EventInfo.builder()
                .title(this.title)
                .content(this.content)
                .startDateTime(this.startDateTime)
                .location(this.location)
                .capacity(this.capacity)
                .posterImageName(posterImageName)
                .activityArea(this.activityArea)
                .recruitmentTarget(this.recruitmentTarget)
                .recruitmentLimit(this.recruitmentLimit)
                .endDateTime(this.endDateTime)
                .dues(this.dues)
                .build();
    }

    private void validate(String title, String content, String location, Integer capacity) {
        if (title == null) throw new IllegalArgumentException(INVALID_EVENT_TITLE.toString());

        boolean invalidTitleLength = title.length() > TITLE_MAX_LENGTH || title.isBlank();
        if (invalidTitleLength) throw new IllegalArgumentException(INVALID_EVENT_TITLE.toString());

        if (content != null) {
            boolean invalidContentLength = content.length() > CONTENT_MAX_LENGTH || content.isBlank();
            if (invalidContentLength) throw new IllegalArgumentException(INVALID_EVENT_CONTENT.toString());
        }

        if (location != null) {
            boolean invalidLocationLength = location.length() > LOCATION_MAX_LENGTH || location.isBlank();
            if (invalidLocationLength) throw new IllegalArgumentException(INVALID_EVENT_LOCATION.toString());
        }

        if (capacity != null) {
            boolean invalidCapacityLength = capacity < CAPACITY_MIN_LENGTH || capacity > CAPACITY_MAX_LENGTH;
            if (invalidCapacityLength) throw new IllegalArgumentException(INVALID_EVENT_CAPACITY.toString());
        }
    }

}
