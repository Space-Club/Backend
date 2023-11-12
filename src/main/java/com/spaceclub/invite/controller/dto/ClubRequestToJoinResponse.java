package com.spaceclub.invite.controller.dto;

import com.spaceclub.club.domain.Club;
import lombok.Builder;

public record ClubRequestToJoinResponse(
        Long clubId,
        String name,
        String info,
        Long memberCount,
        String logoImageUrl
) {

    @Builder
    public ClubRequestToJoinResponse {
    }

    public static ClubRequestToJoinResponse from(Club club, Long memberCount) {
        return ClubRequestToJoinResponse.builder()
                .clubId(club.getId())
                .name(club.getName())
                .info(club.getInfo())
                .memberCount(memberCount)
                .logoImageUrl(club.getLogoImageUrl())
                .build();
    }

}
