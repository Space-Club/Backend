package com.spaceclub.club.controller.dto;

import com.spaceclub.club.domain.ClubNotice;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

public record ClubGetResponse(
        String name,
        String info,
        Long memberCount,
        String image,
        String[] notices
) {

    @Builder
    public ClubGetResponse(String name, String info, Long memberCount, String image, List<ClubNotice> notices) {
        this(
                name,
                info,
                memberCount,
                image,
                notices.stream()
                        .map(ClubNotice::getNotice)
                        .toArray(String[]::new));
    }

}
