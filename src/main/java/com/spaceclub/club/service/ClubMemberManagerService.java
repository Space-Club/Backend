package com.spaceclub.club.service;

import com.spaceclub.club.domain.Club;
import com.spaceclub.club.domain.ClubUser;
import com.spaceclub.club.repository.ClubUserRepository;
import com.spaceclub.club.service.vo.ClubUserUpdate;
import com.spaceclub.club.service.vo.MemberGetInfo;
import com.spaceclub.user.service.UserService;
import com.spaceclub.user.service.vo.UserProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static com.spaceclub.club.ClubExceptionMessage.CAN_NOT_SELF_DEGRADING;
import static com.spaceclub.club.ClubExceptionMessage.CAN_NOT_WITHDRAW;
import static com.spaceclub.club.ClubExceptionMessage.NOT_CLUB_MEMBER;
import static com.spaceclub.club.ClubExceptionMessage.UNAUTHORIZED;
import static com.spaceclub.club.domain.ClubUserRole.MANAGER;
import static com.spaceclub.club.domain.ClubUserRole.MEMBER;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ClubMemberManagerService {

    private static final int MANAGER_MIN_COUNT = 1;

    private final ClubUserRepository clubUserRepository;

    private final UserService userService;

    @Transactional
    public void updateMemberRole(ClubUserUpdate updateVo) {
        this.validManager(updateVo.clubId(), updateVo.userId());

        int count = clubUserRepository.countByClub_IdAndRole(updateVo.clubId(), MANAGER);

        if (isSelfDegrading(count, updateVo)) {
            throw new IllegalArgumentException(CAN_NOT_SELF_DEGRADING.toString());
        }

        ClubUser clubMember = this.getClubUser(updateVo.clubId(), updateVo.memberId());
        ClubUser updateClubUser = clubMember.updateRole(updateVo.role());

        clubUserRepository.save(updateClubUser);
    }

    public List<MemberGetInfo> getMembers(Long clubId, Long userId) {
        clubUserRepository.findByClub_IdAndUserId(clubId, userId)
                .orElseThrow(() -> new IllegalArgumentException(NOT_CLUB_MEMBER.toString()));

        return clubUserRepository.findByClub_Id(clubId).stream()
                .map((member -> {
                    UserProfile userProfile = userService.getProfile(member.getUserId());
                    return MemberGetInfo.from(member, userProfile);
                }))
                .sorted(MemberGetInfo.memberComparator)
                .toList();
    }

    public Long countMember(Club club) {
        return clubUserRepository.countByClub(club);
    }

    public String getUserRole(Long clubId, Long userId) {
        Optional<ClubUser> clubUser = clubUserRepository.findByClub_IdAndUserId(clubId, userId);

        return clubUser.map(user -> user.getRole().name())
                .orElse(null);

    }

    @Transactional
    public void exitClub(Long clubId, Long memberId) {
        ClubUser clubUser = this.getClubUser(clubId, memberId);
        if (isLastManager(clubId, clubUser)) throw new IllegalStateException(CAN_NOT_WITHDRAW.toString());

        clubUserRepository.delete(clubUser);
    }

    @Transactional
    public void deleteMember(Long clubId, Long memberId, Long userId) {
        this.validManager(clubId, userId);

        int count = clubUserRepository.countByClub_IdAndRole(clubId, MANAGER);

        boolean isLastManager = count == MANAGER_MIN_COUNT && userId.equals(memberId);

        if (isLastManager) {
            throw new IllegalArgumentException(CAN_NOT_WITHDRAW.toString());
        }

        ClubUser clubMember = this.getClubUser(clubId, memberId);

        if (clubMember.isManager()) throw new IllegalStateException(UNAUTHORIZED.toString());

        clubUserRepository.delete(clubMember);
    }

    private boolean isSelfDegrading(int count, ClubUserUpdate updateVo) {
        boolean isSoleManager = count == MANAGER_MIN_COUNT;
        return isSoleManager &&
                updateVo.userId().equals(updateVo.memberId()) &&
                updateVo.role() == MEMBER;
    }

    private void validManager(Long clubId, Long userId) {
        ClubUser clubUser = getClubUser(clubId, userId);
        if (clubUser.isNotManager()) throw new IllegalStateException(UNAUTHORIZED.toString());
    }

    private boolean isLastManager(Long clubId, ClubUser clubUser) {
        return clubUser.isManager() &&
                clubUserRepository.countByClub_IdAndRole(clubId, MANAGER) <= MANAGER_MIN_COUNT;
    }

    private ClubUser getClubUser(Long clubId, Long userId) {
        return clubUserRepository.findByClub_IdAndUserId(clubId, userId)
                .orElseThrow(() -> new IllegalStateException(NOT_CLUB_MEMBER.toString()));
    }

    public boolean isAlreadyJoined(Club club, Long userId) {
        return clubUserRepository.existsByClub_IdAndUserId(club.getId(), userId);
    }

    @Transactional
    public void save(ClubUser clubUser) {
        clubUserRepository.save(clubUser);
    }

}
