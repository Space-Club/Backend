package com.spaceclub.club.service;

import com.spaceclub.club.InvitationCodeGenerator;
import com.spaceclub.club.domain.Club;
import com.spaceclub.club.domain.ClubUser;
import com.spaceclub.club.domain.ClubUserRole;
import com.spaceclub.club.repository.ClubRepository;
import com.spaceclub.club.repository.ClubUserRepository;
import com.spaceclub.club.service.vo.ClubUserUpdate;
import com.spaceclub.event.domain.Event;
import com.spaceclub.event.repository.EventRepository;
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

    private final ClubRepository clubRepository;

    private final EventRepository eventRepository;

    private final ClubUserRepository clubUserRepository;

    private final InvitationCodeGenerator codeGenerator;

    public Club createClub(Club club) {
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
        if (count == 1) {
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

}
