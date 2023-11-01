package com.spaceclub.club.service.vo;

import com.spaceclub.club.domain.ClubUserRole;

public record ClubUserUpdate(
        Long clubId,
        Long memberId,
        ClubUserRole role

) {

}
