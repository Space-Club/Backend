package com.spaceclub.club.controller.dto;

import com.spaceclub.club.domain.Club;

public record ClubUpdateRequest(
        String name,
        String info
) {

    public Club toEntity(String logoImageUrl) {
        return Club.builder()
                .name(name)
                .info(info)
                .logoImageUrl(logoImageUrl)
                .build();
    }

}
