package com.spaceclub.club;

import com.spaceclub.club.domain.Club;
import com.spaceclub.club.domain.ClubUser;
import com.spaceclub.club.domain.ClubUserRole;

import static com.spaceclub.user.UserTestFixture.user;

public class ClubTestFixture {

    public static Club club1() {
        return Club.builder()
                .id(1L)
                .name("클럽 명")
                .thumbnailUrl("클럽 이미지 URL")
                .info("클럽 정보")
                .owner("클럽 주인")
                .build();
    }

    public static Club club2() {
        return Club.builder()
                .id(2L)
                .name("클럽 명")
                .thumbnailUrl("클럽 이미지 URL")
                .info("클럽 정보")
                .owner("클럽 주인")
                .build();
    }

    public static ClubUser clubUser() {
        return ClubUser.builder()
                .club(club1())
                .user(user())
                .role(ClubUserRole.MANAGER)
                .build();
    }

}
