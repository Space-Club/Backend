package com.spaceclub.club.service.vo;

import lombok.Builder;

public record ClubNoticeDelete(
        Long userId,
        Long clubId,
        Long noticeId
) {

    @Builder
    public ClubNoticeDelete {
    }

}
