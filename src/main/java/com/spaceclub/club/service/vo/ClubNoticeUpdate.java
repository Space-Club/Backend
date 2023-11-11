package com.spaceclub.club.service.vo;

import lombok.Builder;

public record ClubNoticeUpdate(
        Long clubId,
        Long noticeId,
        String notice,
        Long userId
) {

    @Builder
    public ClubNoticeUpdate {
    }

}
