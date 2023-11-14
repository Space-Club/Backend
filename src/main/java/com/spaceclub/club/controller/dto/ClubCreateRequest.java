package com.spaceclub.club.controller.dto;

import com.spaceclub.club.domain.Club;

public record ClubCreateRequest(
        String name,
        String info
) {

    public Club toEntity() {
        return Club.builder()
                .name(name)
                .info(info)
                .build();
    }

}
