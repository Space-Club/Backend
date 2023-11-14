package com.spaceclub.club.controller.dto;

import com.spaceclub.club.domain.Club;

public record ClubUpdateRequest(
        String name,
        String info
) {

    public Club toEntity(Long clubId) {
        return Club.builder()
                .id(clubId)
                .name(name)
                .info(info)
                .build();
    }

}
