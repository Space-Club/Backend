package com.spaceclub.club;

import com.spaceclub.club.domain.ClubUser;
import com.spaceclub.club.domain.ClubUserRole;

import static com.spaceclub.club.ClubTestFixture.club1;
import static com.spaceclub.club.ClubTestFixture.club2;
import static com.spaceclub.user.UserTestFixture.user1;
import static com.spaceclub.user.UserTestFixture.user2;

public class ClubUserTestFixture {

    public static ClubUser club1User1Manager() {
        return ClubUser.builder()
                .club(club1())
                .userId(user1().getId())
                .role(ClubUserRole.MANAGER)
                .build();
    }

    public static ClubUser club1User2Manager() {
        return ClubUser.builder()
                .club(club1())
                .userId(user2().getId())
                .role(ClubUserRole.MANAGER)
                .build();
    }

    public static ClubUser club1User2Member() {
        return ClubUser.builder()
                .club(club1())
                .userId(user2().getId())
                .role(ClubUserRole.MEMBER)
                .build();
    }

    public static ClubUser club2User1Manager() {
        return ClubUser.builder()
                .club(club2())
                .userId(user1().getId())
                .role(ClubUserRole.MANAGER)
                .build();
    }


}
