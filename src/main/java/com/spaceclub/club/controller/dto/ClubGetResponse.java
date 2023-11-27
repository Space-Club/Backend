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
    public ClubGetResponse {
    }

    public static ClubGetResponse from(Club club, String bucketUrl) {
        return ClubGetResponse.builder()
                .name(club.getName())
                .logoImageUrl(club.getLogoImageName() != null ? bucketUrl + club.getLogoImageName() : null)
                .info(club.getInfo())
                .memberCount(club.getClubUser().size())
                .coverImageUrl(club.getCoverImageName() != null ? bucketUrl + club.getCoverImageName() : null)
                .build();
    }

}
