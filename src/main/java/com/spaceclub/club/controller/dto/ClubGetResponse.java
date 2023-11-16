package com.spaceclub.club.controller.dto;

import com.spaceclub.club.domain.Club;
import lombok.Builder;

public record ClubGetResponse(
        String name,
        String logoImageUrl,
        String info,
        int memberCount,
        String coverImageUrl,
        String inviteLink,
        String role
) {

    @Builder
    public ClubGetResponse(String name,
                           String logoImageUrl,
                           String info,
                           int memberCount,
                           String coverImageUrl,
                           String inviteLink,
                           String role
    ) {
        this.name = name;
        this.logoImageUrl = logoImageUrl;
        this.info = info;
        this.memberCount = memberCount;
        this.coverImageUrl = coverImageUrl;
        this.inviteLink = inviteLink;
        this.role = role;
    }

    public static ClubGetResponse from(Club club, String inviteLink, String role){
        return ClubGetResponse.builder()
                .name(club.getName())
                .logoImageUrl(club.getLogoImageUrl())
                .info(club.getInfo())
                .memberCount(club.getClubUser().size())
                .coverImageUrl(club.getCoverImageUrl())
                .inviteLink(inviteLink)
                .role(role)
                .build();
    }

}
