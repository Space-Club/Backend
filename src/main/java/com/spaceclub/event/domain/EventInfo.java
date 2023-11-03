package com.spaceclub.event.domain;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventInfo {

    @Getter
    private String title;

    @Getter
    private String content;

    @Getter
    private LocalDateTime startDate;

    @Getter
    private String location;

    private int capacity;

    @Getter
    private String posterImageUrl;

    @Builder
    private EventInfo(String title, String content, LocalDateTime startDate, String location, int capacity, String posterImageUrl) {
        this.title = title;
        this.content = content;
        this.startDate = startDate;
        this.location = location;
        this.capacity = capacity;
        this.posterImageUrl = posterImageUrl;
    }

}
