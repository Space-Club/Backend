package com.spaceclub.club.service.vo;

import com.spaceclub.club.domain.ClubUserRole;
import lombok.Builder;

public record ClubUserUpdate(
        Long clubId,
        Long memberId,
        ClubUserRole role,
        Long userId

) {

    @Builder
    public ClubUserUpdate(Long clubId, Long memberId, ClubUserRole role, Long userId) {
        this.clubId = clubId;
        this.memberId = memberId;
        this.role = role;
        this.userId = userId;
    }

}
