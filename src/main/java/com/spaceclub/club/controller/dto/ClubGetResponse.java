package com.spaceclub.club.controller.dto;

import com.spaceclub.club.domain.Club;
import lombok.Builder;

public record ClubGetResponse(
        String name,
        String logoImageUrl,
        String info,
        int memberCount,
        String coverImageUrl,
        String inviteLink
) {

    @Builder
    public ClubGetResponse {
    }

    public static ClubGetResponse from(Club club, String inviteLink){
        return ClubGetResponse.builder()
                .name(club.getName())
                .logoImageUrl(club.getLogoImageUrl())
                .info(club.getInfo())
                .memberCount(club.getClubUser().size())
                .coverImageUrl(club.getCoverImageUrl())
                .inviteLink(inviteLink)
                .build();
    }

}
