package com.spaceclub.club.service.vo;

import com.spaceclub.club.domain.Club;

public record ClubInfo(Long id, String logoImageUrl, String name) {

    public static ClubInfo from(Club club, String bucketUrl) {
        return new ClubInfo(
                club.getId(),
                club.getLogoImageName() != null ? bucketUrl + club.getLogoImageName() : null,
                club.getName()
        );
    }

}
