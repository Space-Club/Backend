package com.spaceclub.club;

import com.spaceclub.club.domain.Club;

public class ClubTestFixture {

    public static Club club1() {
        return Club.builder()
                .id(1L)
                .name("클럽 명")
                .logoImageUrl("클럽 이미지 URL")
                .info("클럽 정보")
                .coverImageUrl("클럽 커버 이미지 URL")
                .build();
    }

    public static Club club2() {
        return Club.builder()
                .id(2L)
                .name("클럽 명")
                .logoImageUrl("클럽 이미지 URL")
                .coverImageUrl("클럽 커버 이미지 URL")
                .info("클럽 정보")
                .build();
    }

}
