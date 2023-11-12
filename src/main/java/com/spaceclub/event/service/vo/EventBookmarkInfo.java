package com.spaceclub.event.service.vo;

public record EventBookmarkInfo(Long userId, Long eventId, boolean bookmarkStatus) {

    public static EventBookmarkInfo of(Long eventId, Long userId, boolean bookmark) {
        return new EventBookmarkInfo(userId, eventId, bookmark);
    }

}
