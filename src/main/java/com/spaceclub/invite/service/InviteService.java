package com.spaceclub.invite.service;

import com.spaceclub.club.domain.Club;
import com.spaceclub.club.repository.ClubRepository;
import com.spaceclub.club.repository.ClubUserRepository;
import com.spaceclub.invite.domain.Invite;
import com.spaceclub.invite.repository.InviteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.spaceclub.global.ExceptionCode.CLUB_NOT_FOUND;
import static com.spaceclub.global.ExceptionCode.NOT_CLUB_MEMBER;
import static com.spaceclub.invite.domain.Invite.INVITE_LINK_VALID_HOURS;

@Service
@Transactional
@RequiredArgsConstructor
public class InviteService {

    private final InviteCodeGenerator inviteCodeGenerator;

    private final InviteRepository inviteRepository;

    private final ClubRepository clubRepository;

    private final ClubUserRepository clubUserRepository;

    public String getInviteCode(Long clubId, Long userId) {
        if (!clubUserRepository.existsByClub_IdAndUserId(clubId, userId))
            throw new IllegalArgumentException(NOT_CLUB_MEMBER.toString());

        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new IllegalArgumentException(CLUB_NOT_FOUND.toString()));

        String inviteCode = inviteCodeGenerator.generateCode();

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

}
