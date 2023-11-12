package com.spaceclub.invite.controller.dto;

import com.spaceclub.club.domain.Club;
import lombok.Builder;

public record ClubAskForJoinResponse(
        Long clubId,
        String name,
        String info,
        Long memberCount,
        String logoImageUrl
) {

    @Builder
    public ClubAskForJoinResponse {
    }

    public static ClubAskForJoinResponse from(Club club, Long memberCount) {
        return ClubAskForJoinResponse.builder()
                .clubId(club.getId())
                .name(club.getName())
                .info(club.getInfo())
                .memberCount(memberCount)
                .logoImageUrl(club.getLogoImageUrl())
                .build();
    }

}
