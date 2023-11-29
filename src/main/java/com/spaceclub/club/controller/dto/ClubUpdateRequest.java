package com.spaceclub.club.controller.dto;

import com.spaceclub.club.domain.Club;
import com.spaceclub.global.bad_word_filter.BadWordFilter;

public record ClubUpdateRequest(
        String name,
        String info
) {

    public ClubUpdateRequest() {
        this(null, null);
    }

    public Club toEntity(Long clubId) {
        BadWordFilter.filter(name);
        BadWordFilter.filter(info);
        return Club.builder()
                .id(clubId)
                .name(name)
                .info(info)
                .isUpdate(true)
                .build();
    }

}
