package com.spaceclub.user.controller.dto;

import com.spaceclub.club.domain.Club;
import lombok.Builder;

public record UserClubGetResponse(Long id, String logoImageUrl, String name) {

    @Builder
    public UserClubGetResponse(Long id, String logoImageUrl, String name) {
        this.id = id;
        this.logoImageUrl = logoImageUrl;
        this.name = name;
    }

    public static UserClubGetResponse from(Club club) {
        return UserClubGetResponse.builder()
                .id(club.getId())
                .logoImageUrl(club.getLogoImageName())
                .name(club.getName())
                .build();
    }

}
