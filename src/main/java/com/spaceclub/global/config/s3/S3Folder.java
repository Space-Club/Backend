package com.spaceclub.global.config.s3;

import lombok.Getter;

public enum S3Folder {

    EVENT_POSTER("event-poster"),
    LOGO("club-logo"),
    COVER("club-cover"),
    USER_PROFILE("user-profile-image");

    S3Folder(String folder) {
        this.folder = folder;
    }

    @Getter
    private String folder;

}
