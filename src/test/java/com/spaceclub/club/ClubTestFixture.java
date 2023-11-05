package com.spaceclub.club;

import com.spaceclub.club.domain.Club;
import com.spaceclub.club.domain.Invitation;

import java.time.LocalDateTime;

public class ClubTestFixture {

    public static Club club1() {
        return Club.builder()
                .id(1L)
                .name("클럽 명")
                .logoImageUrl("클럽 이미지 URL")
                .info("클럽 정보")
                .owner("클럽 주인")
                .invitation(invitation())
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
                .owner("클럽 주인")
                .invitation(invitation())
                .build();
    }

    public static Invitation invitation() {
        return Invitation.builder()
                .invitationCode("0d9c46be-cf32-405c-8e6f-8cbb6d04f5c3")
                .invitationCodeGeneratedAt(LocalDateTime.of(2023, 11, 11, 10, 30, 30))
                .build();
    }

}
