package com.spaceclub.event.domain;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Lob;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.Assert;

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
    private String managerName;

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
            LocalDateTime endDateTime,
            Integer dues,
            String managerName
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
        this.endDateTime = endDateTime;
        this.dues = dues;
        this.managerName = managerName;
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
                .endDateTime(this.endDateTime)
                .dues(this.dues)
                .managerName(this.managerName)
                .build();
    }

    private void validate(String title, String content, String location, Integer capacity) {
        Assert.notNull(title, INVALID_EVENT_TITLE.toString());

        boolean validateTitleLength = title.length() > TITLE_MAX_LENGTH || title.isBlank();
        if (validateTitleLength) throw new IllegalArgumentException(INVALID_EVENT_TITLE.toString());

        if (content != null) {
            boolean validateContentLength = content.length() > CONTENT_MAX_LENGTH || content.isBlank();
            if (validateContentLength) throw new IllegalArgumentException(INVALID_EVENT_CONTENT.toString());
        }

        if (location != null) {
            boolean validateLocationLength = location.length() > LOCATION_MAX_LENGTH || location.isBlank();
            if (validateLocationLength) throw new IllegalArgumentException(INVALID_EVENT_LOCATION.toString());
        }

        if (capacity != null) {
            boolean validateCapacityLength = capacity < CAPACITY_MIN_LENGTH || capacity > CAPACITY_MAX_LENGTH;
            if (validateCapacityLength) throw new IllegalArgumentException(INVALID_EVENT_CAPACITY.toString());
        }
    }

}
