package com.spaceclub.club.controller.dto;

import com.spaceclub.club.domain.Club;
import lombok.Builder;

public record ClubGetResponse(
        String name,
        String logoImageUrl,
        String info,
        int memberCount,
        String coverImageUrl,
        String inviteUrl
) {

    private static final String INVITE_FIXED_URL = "https://spaceclub.site/api/v1/invite/";

    @Builder
    public ClubGetResponse(String name,
                           String logoImageUrl,
                           String info,
                           int memberCount,
                           String coverImageUrl,
                           String inviteUrl
    ) {
        this.name = name;
        this.logoImageUrl = logoImageUrl;
        this.info = info;
        this.memberCount = memberCount;
        this.coverImageUrl = coverImageUrl;
        this.inviteUrl = inviteUrl;
    }

    public static ClubGetResponse from(Club club) {
        return ClubGetResponse.builder()
                .name(club.getName())
                .logoImageUrl(club.getLogoImageUrl())
                .info(club.getInfo())
                .memberCount(club.getClubUser().size())
                .coverImageUrl(club.getCoverImageUrl())
                .inviteUrl(INVITE_FIXED_URL + club.getInvitationCode())
                .build();
    }

}
