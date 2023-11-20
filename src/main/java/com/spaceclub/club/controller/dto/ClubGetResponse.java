package com.spaceclub.club.controller.dto;

import com.spaceclub.club.domain.Club;
import lombok.Builder;

import static com.spaceclub.club.service.ClubService.CLUB_LOGO_S3_URL;

public record ClubGetResponse(
        String name,
        String logoImageUrl,
        String info,
        int memberCount,
        String coverImageUrl
) {

    @Builder
    public ClubGetResponse {
    }

    public static ClubGetResponse from(Club club) {
        return ClubGetResponse.builder()
                .name(club.getName())
                .logoImageUrl(CLUB_LOGO_S3_URL + club.getLogoImageUrl())
                .info(club.getInfo())
                .memberCount(club.getClubUser().size())
                .coverImageUrl(CLUB_LOGO_S3_URL + club.getCoverImageUrl())
                .build();
    }

}
