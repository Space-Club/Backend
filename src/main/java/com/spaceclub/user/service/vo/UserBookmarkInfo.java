package com.spaceclub.user.service.vo;

public record UserBookmarkInfo(Long userId, Long eventId, boolean bookmarkStatus) {

    public static UserBookmarkInfo of(Long eventId, Long userId, boolean bookmark) {
        return new UserBookmarkInfo(userId, eventId, bookmark);
    }

}
