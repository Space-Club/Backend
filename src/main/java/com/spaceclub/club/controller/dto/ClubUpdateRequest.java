package com.spaceclub.club.controller.dto;

import com.spaceclub.club.domain.Club;

public record ClubUpdateRequest(
        String name,
        String info
) {

    public ClubUpdateRequest() {
        this(null, null);
    }

    public Club toEntity(Long clubId) {
        return Club.builder()
                .id(clubId)
                .name(name)
                .info(info)
                .isUpdate(true)
                .build();
    }

}
