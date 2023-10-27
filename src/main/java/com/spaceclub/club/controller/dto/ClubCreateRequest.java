package com.spaceclub.club.controller.dto;

import com.spaceclub.club.domain.Club;

public record ClubCreateRequest(
        String name,
        String info,
        String owner,
        String image
) {

    public Club toEntity() {
        return Club.builder()
                .name(name)
                .info(info)
                .owner(owner)
                .image(image)
                .build();
    }

}
