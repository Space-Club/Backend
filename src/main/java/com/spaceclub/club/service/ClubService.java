package com.spaceclub.club.service;

import com.spaceclub.club.domain.Club;
import com.spaceclub.club.domain.ClubNotice;
import com.spaceclub.club.domain.ClubUser;
import com.spaceclub.club.repository.ClubNoticeRepository;
import com.spaceclub.club.repository.ClubRepository;
import com.spaceclub.club.repository.ClubUserRepository;
import com.spaceclub.club.service.vo.ClubNoticeDelete;
import com.spaceclub.club.service.vo.ClubNoticeUpdate;
import com.spaceclub.club.service.vo.ClubUserUpdate;
import com.spaceclub.event.domain.Event;
import com.spaceclub.event.domain.EventCategory;
import com.spaceclub.event.repository.EventRepository;
import com.spaceclub.global.S3ImageUploader;
import com.spaceclub.user.domain.User;
import com.spaceclub.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.spaceclub.club.domain.ClubUserRole.MANAGER;
import static com.spaceclub.club.domain.ClubUserRole.MEMBER;
import static com.spaceclub.global.ExceptionCode.CAN_NOT_WITHDRAW;
import static com.spaceclub.global.ExceptionCode.CLUB_NOT_FOUND;
import static com.spaceclub.global.ExceptionCode.NOTICE_NOT_FOUND;
import static com.spaceclub.global.ExceptionCode.NOT_CLUB_MEMBER;
import static com.spaceclub.global.ExceptionCode.UNAUTHORIZED;
import static com.spaceclub.global.ExceptionCode.USER_NOT_FOUND;

@Service
@Transactional
@RequiredArgsConstructor
public class ClubService {

    private static final int MANAGER_MIN_COUNT = 1;

    private final ClubRepository clubRepository;

    private final EventRepository eventRepository;

    private final ClubUserRepository clubUserRepository;

    private final UserRepository userRepository;

    private final ClubNoticeRepository clubNoticeRepository;

    private final S3ImageUploader imageUploader;

    public static final String CLUB_LOGO_S3_URL = "https://space-club-image-bucket.s3.ap-northeast-2.amazonaws.com/club-logo/";

    public Club createClub(Club club, Long userId, MultipartFile logoImage) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException(USER_NOT_FOUND.toString()));

        if (logoImage != null) {
            String logoImageUrl = imageUploader.uploadClubLogoImage(logoImage);
            club = club.addLogoImageUrl(logoImageUrl);
        }

        ClubUser clubUser = ClubUser.builder()
                .club(club)
                .user(user)
                .role(MANAGER)
                .build();

        clubUserRepository.save(clubUser);
        return clubRepository.save(club);
    }

    public Club getClub(Long clubId, Long userId) {
        ClubUser clubUser = validateClubUser(clubId, userId);

        return clubUser.getClub();
    }

    public void deleteClub(Long clubId, Long userId) {
        ClubUser clubUser = clubUserRepository.findByClub_IdAndUser_Id(clubId, userId)
                .orElseThrow(() -> new IllegalArgumentException(NOT_CLUB_MEMBER.toString()));

        if (clubUser.isNotManager()) throw new IllegalStateException(UNAUTHORIZED.toString());

        clubRepository.deleteById(clubId);
    }

    public Page<Event> getClubEvents(Long clubId, Pageable pageable, Long userId) {
        validateClubUser(clubId, userId);

        return eventRepository.findByClub_Id(clubId, pageable);
    }

    public void updateMemberRole(ClubUserUpdate updateVo) {
        validateClubManager(updateVo.clubId(), updateVo.userId());

        int count = clubUserRepository.countByClub_IdAndRole(updateVo.clubId(), MANAGER);
        if (count == MANAGER_MIN_COUNT && updateVo.userId().equals(updateVo.memberId()) && updateVo.role() == MEMBER) {
            throw new IllegalArgumentException(CAN_NOT_WITHDRAW.toString());
        }

        ClubUser clubMember = validateClubUser(updateVo.clubId(), updateVo.memberId());
        ClubUser updateClubUser = clubMember.updateRole(updateVo.role());

        clubUserRepository.save(updateClubUser);
    }

    public List<ClubUser> getMembers(Long clubId, Long userId) {
        validateClubManager(clubId, userId);

        return clubUserRepository.findByClub_Id(clubId);
    }

    public void deleteMember(Long clubId, Long memberId, Long userId) {
        validateClubManager(clubId, userId);

        int count = clubUserRepository.countByClub_IdAndRole(clubId, MANAGER);
        if (isLastManager(memberId, userId, count)) {
            throw new IllegalArgumentException(CAN_NOT_WITHDRAW.toString());
        }

        ClubUser clubMember = validateClubUser(clubId, memberId);

        if (clubMember.isManager()) throw new IllegalStateException("관리자는 탈퇴가 불가합니다.");

        clubUserRepository.delete(clubMember);
    }

    private boolean isLastManager(Long memberId, Long userId, int count) {
        return count == MANAGER_MIN_COUNT && userId.equals(memberId);
    }

    public void createNotice(String notice, Long clubId, Long userId) {
        ClubUser clubUser = validateClubUser(clubId, userId);

        if (clubUser.isNotManager()) throw new IllegalStateException(UNAUTHORIZED.toString());

        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new IllegalArgumentException(CLUB_NOT_FOUND.toString()));

        ClubNotice clubNotice = ClubNotice.builder()
                .club(club)
                .notice(notice)
                .build();

        club.getNotices().add(clubNotice);
        clubRepository.save(club);
        clubNoticeRepository.save(clubNotice);
    }

    public List<ClubNotice> getNotices(Long clubId, Long userId) {
        validateClubUser(clubId, userId);

        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new IllegalArgumentException(CLUB_NOT_FOUND.toString()));

        return club.getNotices();
    }

    public void updateNotice(ClubNoticeUpdate updateVo) {
        Long clubId = updateVo.clubId();
        Long userId = updateVo.userId();
        Long noticeId = updateVo.noticeId();

        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new IllegalArgumentException(CLUB_NOT_FOUND.toString()));

        ClubUser clubUser = clubUserRepository.findByClub_IdAndUser_Id(clubId, userId)
                .orElseThrow(() -> new IllegalArgumentException(NOT_CLUB_MEMBER.toString()));

        if (clubUser.isNotManager()) throw new IllegalStateException(UNAUTHORIZED.toString());

        ClubNotice clubNotice = clubNoticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException(NOTICE_NOT_FOUND.toString()));
        List<ClubNotice> notices = club.getNotices();

        for (ClubNotice notice : notices) {
            if (notice.getId().equals(noticeId)) {
                notice.updateNotice(updateVo.notice());
                break;
            }
        }
        ClubNotice newClubNotice = clubNotice.updateNotice(updateVo.notice());

        clubRepository.save(club);
        clubNoticeRepository.save(newClubNotice);
    }

    public void deleteNotice(ClubNoticeDelete deleteVo) {
        Long userId = deleteVo.userId();
        Long clubId = deleteVo.clubId();
        Long noticeId = deleteVo.noticeId();

        ClubUser clubUser = validateClubUser(clubId, userId);

        if (clubUser.isNotManager()) throw new IllegalStateException(UNAUTHORIZED.toString());

        clubNoticeRepository.deleteById(noticeId);
    }


    public List<Event> getClubSchedules(Long clubId, Long userId) {
        validateClubUser(clubId, userId);

        return eventRepository.findAllByClub_IdAndCategory(clubId, EventCategory.CLUB);
    }

    public void updateClub(Club newClub, Long userId, MultipartFile logoImage) {
        Long clubId = newClub.getId();

        validateClubManager(clubId, userId);

        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new IllegalArgumentException(CLUB_NOT_FOUND.toString()));

        if (logoImage != null) {
            String logoImageUrl = CLUB_LOGO_S3_URL + imageUploader.uploadClubLogoImage(logoImage);
            club = club.addLogoImageUrl(logoImageUrl);
        }

        Club updatedClub = club.update(newClub);

        clubRepository.save(updatedClub);
    }

    public Long countMember(Club club) {
        return clubUserRepository.countByClub(club);
    }

    public String getUserRole(Long clubId, Long userId) {
        ClubUser clubUser = clubUserRepository.findByClub_IdAndUser_Id(clubId, userId)
                .orElseThrow(() -> new IllegalArgumentException(NOT_CLUB_MEMBER.toString()));

        return clubUser.getRole().name();
    }

    private ClubUser validateClubUser(Long clubId, Long userId) {
        if (!clubRepository.existsById(clubId)) throw new IllegalStateException(CLUB_NOT_FOUND.toString());
        if (!userRepository.existsById(userId)) throw new IllegalStateException(USER_NOT_FOUND.toString());

        return clubUserRepository.findByClub_IdAndUser_Id(clubId, userId)
                .orElseThrow(() -> new IllegalStateException(NOT_CLUB_MEMBER.toString()));
    }

    private void validateClubManager(Long clubId, Long userId) {
        ClubUser clubUser = validateClubUser(clubId, userId);
        if (clubUser.isNotManager()) throw new IllegalStateException(UNAUTHORIZED.toString());
    }

    public void deleteMember(Long clubId, Long userId) {
        ClubUser clubUser = validateClubUser(clubId, userId);
        if (isLastManager(clubId, clubUser)) throw new IllegalStateException("마지막 관리자는 탈퇴가 불가합니다.");

        clubUserRepository.delete(clubUser);
    }

    private boolean isLastManager(Long clubId, ClubUser clubUser) {
        return clubUser.isManager() &&
                clubUserRepository.countByClub_IdAndRole(clubId, MANAGER) <= MANAGER_MIN_COUNT;
    }

}
