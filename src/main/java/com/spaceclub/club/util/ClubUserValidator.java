package com.spaceclub.club.util;

import com.spaceclub.club.domain.ClubUser;
import com.spaceclub.club.repository.ClubRepository;
import com.spaceclub.club.repository.ClubUserRepository;
import com.spaceclub.user.service.UserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.spaceclub.club.ClubExceptionMessage.CLUB_NOT_FOUND;
import static com.spaceclub.club.ClubExceptionMessage.NOT_CLUB_MEMBER;
import static com.spaceclub.club.ClubExceptionMessage.UNAUTHORIZED;

@Component
@RequiredArgsConstructor
public class ClubUserValidator {

    private final ClubRepository clubRepository;

    private final ClubUserRepository clubUserRepository;

    private final UserProvider userProvider;

    public void validateClubManager(Long clubId, Long userId) {
        if (!clubRepository.existsById(clubId)) throw new IllegalStateException(CLUB_NOT_FOUND.toString());
        userProvider.validateUser(userId);

        ClubUser clubUser = clubUserRepository.findByClub_IdAndUserId(clubId, userId)
                .orElseThrow(() -> new IllegalStateException(NOT_CLUB_MEMBER.toString()));

        if (clubUser.isNotManager()) throw new IllegalStateException(UNAUTHORIZED.toString());
    }

    public void validateClubMember(Long clubId, Long userId) {
        if (!clubRepository.existsById(clubId)) throw new IllegalStateException(CLUB_NOT_FOUND.toString());
        userProvider.validateUser(userId);

        if (!clubUserRepository.existsByClub_IdAndUserId(clubId, userId))
            throw new IllegalArgumentException(NOT_CLUB_MEMBER.toString());
    }

}
