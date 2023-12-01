package com.spaceclub.club.util;

import com.spaceclub.club.domain.Club;
import com.spaceclub.global.bad_word_filter.BadWordFilter;

import static com.spaceclub.club.ClubExceptionMessage.EXCEED_NOTICE_LENGTH;
import static com.spaceclub.club.ClubExceptionMessage.NOTICE_NOT_NULL;
import static com.spaceclub.club.ClubExceptionMessage.NOTICE_WITH_BLANK;
import static com.spaceclub.club.ClubExceptionMessage.NOTICE_WITH_MARGIN;

public class ClubValidator {

    public static void validateClub(Club club) {
        BadWordFilter.filter(club.getName());
        BadWordFilter.filter(club.getInfo());
    }

    public static void validateNotice(String notice) {
        if (notice == null) throw new IllegalArgumentException(NOTICE_NOT_NULL.toString());
        if (notice.isBlank()) throw new IllegalArgumentException(NOTICE_WITH_BLANK.toString());
        if (!notice.strip().equals(notice)) throw new IllegalArgumentException(NOTICE_WITH_MARGIN.toString());
        if (notice.length() > 1000) throw new IllegalArgumentException(EXCEED_NOTICE_LENGTH.toString());
        BadWordFilter.filter(notice);
    }

}
