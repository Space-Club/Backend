package com.spaceclub.club.controller;

import com.spaceclub.club.domain.Club;
import lombok.Builder;

public record ClubGetAllResponse(Long id, String logoImageUrl, String name) {

    @Builder
    public ClubGetAllResponse(Long id, String logoImageUrl, String name) {
        this.id = id;
        this.logoImageUrl = logoImageUrl;
        this.name = name;
    }

    public static ClubGetAllResponse from(Club club) {
        return ClubGetAllResponse.builder()
                .id(club.getId())
                .logoImageUrl(club.getLogoImageUrl())
                .name(club.getName())
                .build();
    }

}
