package com.spaceclub.club;

import com.spaceclub.club.domain.Club;

public class ClubTestFixture {

    public static Club club() {
        return Club.builder()
                .name("클럽 명")
                .image("클럽 이미지 URL")
                .info("클럽 정보")
                .owner("클럽 주인")
                .build();
    }

}
