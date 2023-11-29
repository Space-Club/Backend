package com.spaceclub.club.controller.dto;

import com.spaceclub.club.domain.Club;
import com.spaceclub.global.bad_word_filter.BadWordFilter;

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
