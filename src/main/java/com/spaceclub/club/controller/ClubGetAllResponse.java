package com.spaceclub.club.controller;

import com.spaceclub.club.domain.Club;
import lombok.Builder;

public record ClubGetAllResponse(Long id, String image, String name) {

    @Builder
    public ClubGetAllResponse(Long id, String image, String name) {
        this.id = id;
        this.image = image;
        this.name = name;
    }

    public static ClubGetAllResponse from(Club club) {
        return ClubGetAllResponse.builder()
                .id(club.getId())
                .image(club.getThumbnailUrl())
                .name(club.getName())
                .build();
    }

}
