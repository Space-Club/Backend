package com.spaceclub.club.controller.dto;

import com.spaceclub.club.domain.Club;

public record ClubCreateRequest(
        String name,
        String info,
        String owner
) {

    public Club toEntity(String thumbnailUrl) {
        return Club.builder()
                .name(name)
                .info(info)
                .owner(owner)
                .thumbnailUrl(thumbnailUrl)
                .build();
    }

}
