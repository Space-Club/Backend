package com.spaceclub.club.controller.dto;

import com.spaceclub.club.domain.Club;
import lombok.Builder;

public record ClubGetResponse(
        String name,
        String logoImageUrl,
        String info,
        int memberCount,
        String coverImageUrl
) {

    @Builder
    public ClubGetResponse(String name,
                           String logoImageUrl,
                           String info,
                           int memberCount,
                           String coverImageUrl
    ) {
        this.name = name;
        this.logoImageUrl = logoImageUrl;
        this.info = info;
        this.memberCount = memberCount;
        this.coverImageUrl = coverImageUrl;
    }

    public static ClubGetResponse from(Club club) {
        return ClubGetResponse.builder()
                .name(club.getName())
                .logoImageUrl(club.getLogoImageUrl())
                .info(club.getInfo())
                .memberCount(club.getClubUser().size())
                .coverImageUrl(club.getCoverImageUrl())
                .build();
    }

}
