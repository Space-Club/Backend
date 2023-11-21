package com.spaceclub.club.service.vo;

import com.spaceclub.club.domain.Club;

public record ClubInfo(Long id, String logoImageUrl, String name) {

    public static ClubInfo from(Club club) {
        return new ClubInfo(
                club.getId(),
                club.getLogoImageUrl(),
                club.getName()
        );
    }

}
