package com.spaceclub.invite;

import com.spaceclub.invite.domain.Invite;

import java.time.LocalDateTime;

import static com.spaceclub.club.ClubTestFixture.club1;
import static com.spaceclub.club.ClubTestFixture.club2;

public class InviteTestFixture {

    public static Invite invite1() {
        return Invite.builder()
                .code("this-is-code")
                .club(club1())
                .expiredAt(LocalDateTime.of(2001, 10, 1, 10, 0))
                .build();
    }

    public static Invite invite2() {
        return Invite.builder()
                .code("this-is-future-code")
                .club(club2())
                .expiredAt(LocalDateTime.of(2999, 10, 1, 10, 0))
                .build();
    }

}
