package com.spaceclub.global.s3;

import lombok.Getter;

@Getter
public enum S3Folder {

    EVENT_POSTER("event-poster"),
    LOGO("club-logo"),
    COVER("club-cover"),
    USER_PROFILE("user-profile-image"),
    POST_IMAGE("post-image");

    S3Folder(String folder) {
        this.folder = folder;
    }

    private final String folder;

}
