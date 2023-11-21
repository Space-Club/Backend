package com.spaceclub.invite.service;

import com.spaceclub.club.domain.Club;
import com.spaceclub.club.domain.ClubUser;
import com.spaceclub.club.domain.ClubUserRole;
import com.spaceclub.club.repository.ClubUserRepository;
import com.spaceclub.club.service.ClubMemberManagerService;
import com.spaceclub.invite.domain.Invite;
import com.spaceclub.invite.repository.InviteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.spaceclub.global.ExceptionCode.CLUB_ALREADY_JOINED;
import static com.spaceclub.global.ExceptionCode.INVITE_EXPIRED;
import static com.spaceclub.global.ExceptionCode.INVITE_NOT_FOUND;

@Service
@Transactional
@RequiredArgsConstructor
public class InviteJoinService {

    private final InviteRepository inviteRepository;

    private final ClubMemberManagerService clubMemberManagerService;

    public Long joinClub(String code, Long userId) {
        Invite invite = inviteRepository.findByCode(code)
                .orElseThrow(() -> new IllegalStateException(INVITE_NOT_FOUND.toString()));

        if (invite.isExpired()) throw new IllegalStateException(INVITE_EXPIRED.toString());

        Club club = invite.getClub();

        if (clubMemberManagerService.isAlreadyJoined(club, userId))
            throw new IllegalStateException(CLUB_ALREADY_JOINED.toString());

        ClubUser clubUser = ClubUser.builder()
                .club(club)
                .userId(userId)
                .role(ClubUserRole.MEMBER)
                .build();

        clubMemberManagerService.save(clubUser);

        return club.getId();
    }

    public Club requestToJoinClub(String code) {
        Invite invite = inviteRepository.findByCode(code)
                .orElseThrow(() -> new IllegalStateException(INVITE_NOT_FOUND.toString()));

        return invite.getClub();
    }

}
