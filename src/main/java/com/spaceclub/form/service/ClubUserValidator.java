package com.spaceclub.form.service;

import com.spaceclub.club.domain.Club;
import com.spaceclub.club.domain.ClubUser;
import com.spaceclub.club.repository.ClubRepository;
import com.spaceclub.club.repository.ClubUserRepository;
import com.spaceclub.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.spaceclub.global.ExceptionCode.CLUB_NOT_FOUND;
import static com.spaceclub.global.ExceptionCode.NOT_CLUB_MEMBER;
import static com.spaceclub.global.ExceptionCode.UNAUTHORIZED;
import static com.spaceclub.global.ExceptionCode.USER_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class ClubUserValidator {

    private final ClubRepository clubRepository;

    private final ClubUserRepository clubUserRepository;

    private final UserRepository userRepository;

    public Club validateClubManager(Long clubId, Long userId) {
        if (!clubRepository.existsById(clubId)) throw new IllegalStateException(CLUB_NOT_FOUND.toString());
        if (!userRepository.existsById(userId)) throw new IllegalStateException(USER_NOT_FOUND.toString());

        ClubUser clubUser = clubUserRepository.findByClub_IdAndUserId(clubId, userId)
                .orElseThrow(() -> new IllegalStateException(NOT_CLUB_MEMBER.toString()));
        if (clubUser.isNotManager()) throw new IllegalStateException(UNAUTHORIZED.toString());

        return clubUser.getClub();
    }

    public void validateClubMember(Long clubId, Long userId) {
        if (!clubRepository.existsById(clubId)) throw new IllegalStateException(CLUB_NOT_FOUND.toString());
        if (!userRepository.existsById(userId)) throw new IllegalStateException(USER_NOT_FOUND.toString());

        if (clubUserRepository.existsByClub_IdAndUserId(clubId, userId))
            throw new IllegalArgumentException(NOT_CLUB_MEMBER.toString());
    }

}
