package com.spaceclub.club.controller.dto;

import com.spaceclub.club.domain.Club;
import com.spaceclub.club.domain.ClubNotice;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;

public record ClubGetResponse(
        String name,
        String info,
        Long memberCount,
        String image,
        List<String> notices
) {

    @Builder
    public ClubGetResponse(String name, String info, Long memberCount, String image, List<String> notices) {
        this.name = name;
        this.info = info;
        this.memberCount = memberCount;
        this.image = image;
        this.notices = new ArrayList<>(notices);
    }

    public static ClubGetResponse from(Club club) {
        long memberCount = club.getClubUser().stream()
                .filter((user) -> user.getClub().getId().equals(club.getId()))
                .count();

        return ClubGetResponse.builder()
                .name(club.getName())
                .info(club.getInfo())
                .memberCount(memberCount)
                .image(club.getThumbnailUrl())
                .notices(club.getNotices().stream()
                        .map(ClubNotice::getNotice)
                        .toList())
                .build();
    }

}
