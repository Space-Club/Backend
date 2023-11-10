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
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 클럽이 없습니다."));

        String inviteCode = codeGenerator.generateCode();

        Invite invite = inviteRepository.findByClub(club)
                .orElse(
                        Invite.builder()
                                .code(inviteCode)
                                .club(club)
                                .build()
                );

        inviteRepository.save(invite);

        return invite.getCode();
    }

    public void joinClub(String code, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 유저가 없습니다."));

        Invite invite = inviteRepository.findByCode(code)
                .orElseThrow(() -> new IllegalStateException("해당 초대코드를 보유한 클럽이 없습니다"));

        Club club = invite.getClub();

        ClubUser clubUser = ClubUser.builder()
                .club(club)
                .user(user)
                .role(ClubUserRole.MEMBER)
                .build();

        clubUserRepository.save(clubUser);
    }

}
