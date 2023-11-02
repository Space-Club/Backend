package com.spaceclub.club.service;

import com.spaceclub.club.domain.Club;
import com.spaceclub.club.domain.ClubUser;
import com.spaceclub.club.repository.ClubRepository;
import com.spaceclub.club.repository.ClubUserRepository;
import com.spaceclub.club.service.vo.ClubUserUpdate;
import com.spaceclub.event.domain.Event;
import com.spaceclub.event.repository.EventRepository;
import com.spaceclub.user.domain.User;
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
        clubRepository.findById(updateVo.clubId())
                .orElseThrow(() -> new IllegalArgumentException("해당하는 클럽이 없습니다"));
        ClubUser clubUser = clubUserRepository.findByClub_IdAndUser_Id(updateVo.clubId(), updateVo.memberId())
                .orElseThrow(() -> new IllegalArgumentException("클럽의 멤버가 아닙니다"));

        ClubUser updateClubUser = clubUser.updateRole(updateVo.role());

        clubUserRepository.save(updateClubUser);
    }

    public List<ClubUser> getMembers(Long clubId) {
        clubRepository.findById(clubId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 클럽이 없습니다"));

        return clubUserRepository.findByClub_Id(clubId);
    }

}
