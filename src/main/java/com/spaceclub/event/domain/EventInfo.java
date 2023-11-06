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

    private final int TITLE_MAX_LENGTH = 30;

    private final int CONTENT_MAX_LENGTH = 200;

    private final int LOCATION_MAX_LENGTH = 30;

    private final int CAPACITY_MIN_LENGTH = 1;

    private final int CAPACITY_MAX_LENGTH = 999;

    @Getter
    private String title;

    @Getter
    private String content;

    @Getter
    private LocalDateTime startDate;

    @Getter
    private String location;

    @Getter
    private int capacity;

    @Lob
    @Getter
    private String posterImageUrl;

    @Builder
    private EventInfo(String title, String content, LocalDateTime startDate, String location, int capacity, String posterImageUrl) {
        validate(title, content, startDate, location, capacity, posterImageUrl);
        this.title = title;
        this.content = content;
        this.startDate = startDate;
        this.location = location;
        this.capacity = capacity;
        this.posterImageUrl = posterImageUrl;
    }

    private void validate(String title, String content, LocalDateTime startDate, String location, int capacity, String posterImageUrl) {
        Assert.notNull(title, "행사 제목은 필수값입니다.");
        Assert.notNull(content, "행사 내용은 필수값입니다.");
        Assert.notNull(startDate, "행사 시작 날짜은 필수값입니다.");
        Assert.notNull(location, "행사 위치는 필수값입니다.");
        Assert.notNull(posterImageUrl, "행사 포스터는 필수값입니다.");
        Assert.isTrue(title.length() <= TITLE_MAX_LENGTH && !title.isBlank(), "행사 제목은 1~30자 사이의 길이입니다.");
        Assert.isTrue(content.length() <= CONTENT_MAX_LENGTH && !content.isBlank(), "행사 내용은 1~200자 사이의 길이입니다.");
        Assert.isTrue(location.length() <= LOCATION_MAX_LENGTH && !location.isBlank(), "행사 위치는 1~30자 사이의 길이입니다.");
        Assert.isTrue(capacity >= CAPACITY_MIN_LENGTH && capacity <= CAPACITY_MAX_LENGTH, "행사 정원은 1~999사이의 값입니다.");
    }

}
