package com.spaceclub.invite.service;

import com.spaceclub.club.domain.Club;
import com.spaceclub.club.domain.ClubUser;
import com.spaceclub.club.domain.ClubUserRole;
import com.spaceclub.club.repository.ClubRepository;
import com.spaceclub.club.repository.ClubUserRepository;
import com.spaceclub.invite.domain.Invite;
import com.spaceclub.invite.domain.InviteCodeGenerator;
import com.spaceclub.invite.repository.InviteRepository;
import com.spaceclub.user.domain.User;
import com.spaceclub.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.spaceclub.global.ExceptionCode.CLUB_ALREADY_JOINED;
import static com.spaceclub.global.ExceptionCode.CLUB_NOT_FOUND;
import static com.spaceclub.global.ExceptionCode.INVITE_EXPIRED;
import static com.spaceclub.global.ExceptionCode.INVITE_NOT_FOUND;
import static com.spaceclub.global.ExceptionCode.NOT_CLUB_MEMBER;
import static com.spaceclub.global.ExceptionCode.USER_NOT_FOUND;
import static com.spaceclub.invite.domain.Invite.INVITE_LINK_VALID_HOURS;

@Service
@Transactional
@RequiredArgsConstructor
public class InviteService {

    private final InviteCodeGenerator codeGenerator;

    private final InviteRepository inviteRepository;

    private final ClubRepository clubRepository;

    private final UserRepository userRepository;

    private final ClubUserRepository clubUserRepository;

    public String getInviteCode(Long clubId, Long userId) {
        if (!clubUserRepository.existsByClub_IdAndUser_Id(clubId, userId))
            throw new IllegalArgumentException(NOT_CLUB_MEMBER.toString());

        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new IllegalArgumentException(CLUB_NOT_FOUND.toString()));

        String inviteCode = codeGenerator.generateCode();

        Invite invite = inviteRepository.findByClub(club)
                .orElse(
                        Invite.builder()
                                .code(inviteCode)
                                .club(club)
                                .expiredAt(LocalDateTime.now().plusHours(INVITE_LINK_VALID_HOURS))
                                .build()
                );

        if (invite.isExpired()) {
            invite = invite.updateCode(inviteCode);
        }

        inviteRepository.save(invite);

        return invite.getCode();
    }

    public Long joinClub(String code, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(USER_NOT_FOUND.toString()));

        Invite invite = inviteRepository.findByCode(code)
                .orElseThrow(() -> new IllegalStateException(INVITE_NOT_FOUND.toString()));

        if (invite.isExpired()) throw new IllegalStateException(INVITE_EXPIRED.toString());

        Club club = invite.getClub();

        if (clubUserRepository.existsByClubAndUser(club, user))
            throw new IllegalStateException(CLUB_ALREADY_JOINED.toString());

        ClubUser clubUser = ClubUser.builder()
                .club(club)
                .user(user)
                .role(ClubUserRole.MEMBER)
                .build();

        clubUserRepository.save(clubUser);

        return club.getId();
    }

    public Club requestToJoinClub(String code) {
        Invite invite = inviteRepository.findByCode(code)
                .orElseThrow(() -> new IllegalStateException(INVITE_NOT_FOUND.toString()));

        return invite.getClub();
    }

}
