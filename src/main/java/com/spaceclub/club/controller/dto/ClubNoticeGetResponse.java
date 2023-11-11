package com.spaceclub.club.controller.dto;

import com.spaceclub.club.domain.ClubNotice;

import java.util.List;

public record ClubNoticeGetResponse(
        List<ClubNotice> notices
) {

}
