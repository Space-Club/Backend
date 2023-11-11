package com.spaceclub.club;

import com.spaceclub.club.domain.ClubNotice;

import static com.spaceclub.club.ClubTestFixture.club1;

public class ClubNoticeTestFixture {

    public static ClubNotice clubNotice1() {
        return ClubNotice.builder()
                .id(1L)
                .club(club1())
                .notice("notice 1")
                .build();
    }

    public static ClubNotice clubNotice2() {
        return ClubNotice.builder()
                .id(2L)
                .club(club1())
                .notice("notice 2")
                .build();
    }

}
