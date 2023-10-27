package com.spaceclub.event.domain;

import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Embeddable
@EqualsAndHashCode
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EventInfo {

    @Getter
    private String title;

    @Getter
    private String content;

    private LocalDate startDate;

    @Getter
    private String location;

    private int capacity;

    private String poster;

    @Builder
    private EventInfo(String title, String content, LocalDate startDate, String location, int capacity, String poster) {
        this.title = title;
        this.content = content;
        this.startDate = startDate;
        this.location = location;
        this.capacity = capacity;
        this.poster = poster;
    }

}
