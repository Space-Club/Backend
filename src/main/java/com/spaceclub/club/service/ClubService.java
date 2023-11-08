package com.spaceclub.club.service;

import com.spaceclub.club.InvitationCodeGenerator;
import com.spaceclub.club.domain.Club;
import com.spaceclub.club.domain.ClubUser;
import com.spaceclub.club.domain.ClubUserRole;
import com.spaceclub.club.domain.Invitation;
import com.spaceclub.club.repository.ClubRepository;
import com.spaceclub.club.repository.ClubUserRepository;
import com.spaceclub.club.service.vo.ClubUserUpdate;
import com.spaceclub.event.domain.Event;
import com.spaceclub.event.repository.EventRepository;
import com.spaceclub.user.domain.User;
import com.spaceclub.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ClubService {

    public static final int MANAGER_COUNT = 1;

    private final ClubRepository clubRepository;

    private final EventRepository eventRepository;

    private final ClubUserRepository clubUserRepository;

    private final UserRepository userRepository;

    private final InvitationCodeGenerator codeGenerator;

    public Club createClub(Club club, Long clubId) {
        User user = userRepository.findById(clubId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 유저가 없습니다"));

        ClubUser clubUser = ClubUser.builder()
                .club(club)
                .user(user)
                .role(ClubUserRole.MANAGER)
                .build();

        clubUserRepository.save(clubUser);
        return clubRepository.save(club);
    }

    public Club getClub(Long clubId) {
        return clubRepository.findById(clubId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 클럽이 없습니다"));
    }

    public void deleteClub(Long clubId) {
        clubRepository.deleteById(clubId);
    }

    public Page<Event> getClubEvents(Long clubId, Pageable pageable) {
        return eventRepository.findByClub_Id(clubId, pageable);
    }

    public void updateMemberRole(ClubUserUpdate updateVo) {
        ClubUser clubUser = validateClubMember(updateVo.clubId(), updateVo.memberId());
        ClubUser updateClubUser = clubUser.updateRole(updateVo.role());

        clubUserRepository.save(updateClubUser);
    }

    public List<ClubUser> getMembers(Long clubId) {
        clubRepository.findById(clubId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 클럽이 없습니다"));

        return clubUserRepository.findByClub_Id(clubId);
    }

    public void deleteMember(Long clubId, Long memberId) {
        ClubUser clubUser = validateClubMember(clubId, memberId);

        int count = clubUserRepository.countByClub_IdAndRole(clubId, ClubUserRole.MANAGER);
        if (count == MANAGER_COUNT) {
            throw new IllegalArgumentException("마지막 관리자는 탈퇴가 불가합니다.");
        }

        clubUserRepository.delete(clubUser);
    }

    private ClubUser validateClubMember(Long clubId, Long memberId) {
        clubRepository.findById(clubId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 클럽이 없습니다"));

        return clubUserRepository.findByClub_IdAndUser_Id(clubId, memberId)
                .orElseThrow(() -> new IllegalArgumentException("클럽의 멤버가 아닙니다"));
    }

    public String getInvitationCode(Long clubId) {
        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 클럽이 없습니다."));

        String invitationCode = club.getInvitationCode();
        if (invitationCode == null || invitationCode.equals(InvitationCodeGenerator.getInitValue())) {
            invitationCode = codeGenerator.generateInvitationCode();
            Club newClub = club.assignInvitationCode(invitationCode);

            clubRepository.save(newClub);
        }

        return invitationCode;
    }

    public boolean joinClub(String uuid) {
        User user = userRepository.findById(1L)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 유저가 없습니다."));

        Invitation invitation = Invitation.builder()
                .invitationCode(uuid)
                .build();

        Club club = clubRepository.findByInvitation(invitation)
                .orElseThrow(() -> new IllegalArgumentException("잘못된 초대코드 입니다"));

        String invitationCode = club.getInvitationCode();

        if (invitationCode.equals(uuid)) {
            ClubUser clubUser = ClubUser.builder()
                    .user(user)
                    .club(club)
                    .role(ClubUserRole.MEMBER)
                    .build();
            clubUserRepository.save(clubUser);

            return true;
        }

        return false;
    }

}
