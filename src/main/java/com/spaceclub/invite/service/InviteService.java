package com.spaceclub.invite.service;

import com.spaceclub.club.domain.Club;
import com.spaceclub.club.service.ClubService;
import com.spaceclub.club.service.ClubUserValidator;
import com.spaceclub.invite.domain.Invite;
import com.spaceclub.invite.repository.InviteRepository;
import com.spaceclub.invite.service.util.InviteCodeGenerator;
import com.spaceclub.invite.service.util.UuidCodeGenerator;
import com.spaceclub.invite.service.vo.InviteGetInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.spaceclub.invite.domain.Invite.INVITE_LINK_VALID_HOURS;

@Service
@Transactional
@RequiredArgsConstructor
public class InviteService {

    private final InviteCodeGenerator inviteCodeGenerator = new UuidCodeGenerator();

    private final InviteRepository inviteRepository;

    private final ClubUserValidator clubUserValidator;

    private final ClubService clubService;

    public String createInviteCode(Long clubId, Long userId) {
        Club club = clubService.getClub(clubId, userId);

        String inviteCode = inviteCodeGenerator.generateCode();

        Invite tempInvite = Invite.builder()
                .code(inviteCode)
                .club(club)
                .expiredAt(LocalDateTime.now().plusHours(INVITE_LINK_VALID_HOURS))
                .build();

        Invite invite = inviteRepository.findByClub(club)
                .orElse(tempInvite);

        if (invite.isExpired()) {
            invite = invite.updateCode(inviteCode);
        }

        inviteRepository.save(invite);

        return invite.getCode();
    }

    public InviteGetInfo getInviteLink(Long clubId, Long userId) {
        clubUserValidator.validateClubManager(clubId, userId);

        Optional<Invite> optionalInvite = inviteRepository.findByClub_Id(clubId);

        if (optionalInvite.isEmpty()) {
            return new InviteGetInfo(null, false);
        }

        Invite invite = optionalInvite.get();

        String code = invite.getCode();
        boolean expired = invite.isExpired();

        return new InviteGetInfo(code, expired);
    }

}
