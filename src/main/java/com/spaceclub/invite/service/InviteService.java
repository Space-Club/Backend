package com.spaceclub.invite.service;

import com.spaceclub.club.domain.Club;
import com.spaceclub.club.service.ClubService;
import com.spaceclub.club.service.ClubUserValidator;
import com.spaceclub.invite.domain.Invite;
import com.spaceclub.invite.repository.InviteRepository;
import com.spaceclub.invite.service.vo.InviteGetInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static com.spaceclub.invite.InviteExceptionMessage.INVITE_NOT_FOUND;
import static com.spaceclub.invite.domain.Invite.INVITE_LINK_VALID_HOURS;

@Service
@Transactional
@RequiredArgsConstructor
public class InviteService {

    private final InviteCodeGenerator inviteCodeGenerator;

    private final InviteRepository inviteRepository;

    private final ClubUserValidator clubUserValidator;

    private final ClubService clubService;

    public String createAndGetInviteLink(Long clubId, Long userId) {
        Club club = clubService.getClub(clubId, userId);

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

    public InviteGetInfo getInviteLink(Long clubId, Long userId) {
        clubUserValidator.validateClubManager(clubId, userId);

        Invite invite = inviteRepository.findByClub_Id(clubId)
                .orElseThrow(() -> new IllegalArgumentException(INVITE_NOT_FOUND.toString()));

        String code = invite.getCode();
        boolean expired = invite.isExpired();

        return new InviteGetInfo(code, expired);
    }

}
