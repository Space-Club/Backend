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
    private String posterImageUrl;

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
            String posterImageUrl,
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
        this.posterImageUrl = posterImageUrl;
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
                .posterImageUrl(posterImageName)
                .activityArea(this.activityArea)
                .recruitmentTarget(this.recruitmentTarget)
                .endDateTime(this.endDateTime)
                .dues(this.dues)
                .managerName(this.managerName)
                .build();
    }

    private void validate(String title, String content, String location, Integer capacity) {
        Assert.notNull(title, "행사 내용은 필수값입니다.");

        boolean validateTitleLength = title.length() <= TITLE_MAX_LENGTH && !title.isBlank();
        Assert.isTrue(validateTitleLength, "행사 제목은 1~30자 사이의 길이입니다.");

        if (content != null) {
            boolean validateContentLength = content.length() <= CONTENT_MAX_LENGTH && !content.isBlank();
            Assert.isTrue(validateContentLength, "행사 내용은 1~200자 사이의 길이입니다.");
        }

        if (location != null) {
            boolean validateLocationLength = location.length() <= LOCATION_MAX_LENGTH && !location.isBlank();
            Assert.isTrue(validateLocationLength, "행사 위치는 1~30자 사이의 길이입니다.");
        }

        if (capacity != null) {
            boolean validateCapacityLength = capacity >= CAPACITY_MIN_LENGTH && capacity <= CAPACITY_MAX_LENGTH;
            Assert.isTrue(validateCapacityLength, "행사 정원은 1~999사이의 값입니다.");
        }
    }

}
