package com.spaceclub.club.service;

import com.spaceclub.club.domain.Club;
import com.spaceclub.club.domain.ClubNotice;
import com.spaceclub.club.domain.ClubUser;
import com.spaceclub.club.domain.ClubUserRole;
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

import java.io.IOException;
import java.util.List;

import static com.spaceclub.club.domain.ClubUserRole.MEMBER;

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

    private static final String CLUB_LOGO_S3_URL = "https://space-club-image-bucket.s3.ap-northeast-2.amazonaws.com/club-logo/";

    public Club createClub(Club club, Long userId, MultipartFile logoImage) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 유저가 없습니다"));

        if (logoImage != null) {
            String logoImageUrl = CLUB_LOGO_S3_URL + imageUploader.uploadClubLogoImage(logoImage);
            club = club.addLogoImageUrl(logoImageUrl);
        }

        ClubUser clubUser = ClubUser.builder()
                .club(club)
                .user(user)
                .role(ClubUserRole.MANAGER)
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
                .orElseThrow(() -> new IllegalArgumentException("해당 클럽의 멤버가 아닙니다"));

        if (clubUser.isNotManager()) throw new IllegalStateException("클럽을 삭제할 권한이 없습니다.");

        clubRepository.deleteById(clubId);
    }

    public Page<Event> getClubEvents(Long clubId, Pageable pageable, Long userId) {
        validateClubUser(clubId, userId);

        return eventRepository.findByClub_Id(clubId, pageable);
    }

    public void updateMemberRole(ClubUserUpdate updateVo) {
        validateClubManager(updateVo.clubId(), updateVo.userId());

        int count = clubUserRepository.countByClub_IdAndRole(updateVo.clubId(), ClubUserRole.MANAGER);
        if (count == MANAGER_MIN_COUNT && updateVo.userId().equals(updateVo.memberId()) && updateVo.role() == MEMBER) {
            throw new IllegalArgumentException("마지막 관리자는 탈퇴가 불가합니다.");
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

        int count = clubUserRepository.countByClub_IdAndRole(clubId, ClubUserRole.MANAGER);
        if (isLastManager(memberId, userId, count)) {
            throw new IllegalArgumentException("마지막 관리자는 탈퇴가 불가합니다.");
        }

        ClubUser clubMember = validateClubUser(clubId, memberId);
        clubUserRepository.delete(clubMember);
    }

    private boolean isLastManager(Long memberId, Long userId, int count) {
        return count == MANAGER_MIN_COUNT && userId.equals(memberId);
    }

    public void createNotice(String notice, Long clubId, Long userId) {
        ClubUser clubUser = validateClubUser(clubId, userId);

        if (clubUser.isNotManager()) throw new IllegalStateException("해당 권한이 없습니다.");

        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 클럽이 없습니다."));

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
                .orElseThrow(() -> new IllegalArgumentException("해당하는 클럽이 없습니다."));

        return club.getNotices();
    }

    public void updateNotice(ClubNoticeUpdate updateVo) {
        Long clubId = updateVo.clubId();
        Long userId = updateVo.userId();
        Long noticeId = updateVo.noticeId();

        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 클럽이 없습니다."));

        ClubUser clubUser = clubUserRepository.findByClub_IdAndUser_Id(clubId, userId)
                .orElseThrow(() -> new IllegalArgumentException("클럽의 멤버가 아닙니다."));

        if (clubUser.isNotManager()) throw new IllegalStateException("해당 권한이 없습니다.");

        ClubNotice clubNotice = clubNoticeRepository.findById(noticeId)
                .orElseThrow(() -> new IllegalArgumentException("해당 공지사항이 없습니다."));
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

        if (clubUser.isNotManager()) throw new IllegalStateException("해당 권한이 없습니다.");

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
                .orElseThrow(() -> new IllegalArgumentException("해당하는 클럽이 없습니다."));

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
                .orElseThrow(() -> new IllegalArgumentException("해당하는 유저가 없습니다."));

        return clubUser.getRole().name();
    }

    private ClubUser validateClubUser(Long clubId, Long userId) {
        if (!clubRepository.existsById(clubId)) throw new IllegalStateException("존재하지 않는 클럽입니다.");
        if (!userRepository.existsById(userId)) throw new IllegalStateException("존재하지 않는 유저입니다.");

        return clubUserRepository.findByClub_IdAndUser_Id(clubId, userId)
                .orElseThrow(() -> new IllegalStateException("클럽의 멤버가 아닙니다."));
    }

    private void validateClubManager(Long clubId, Long userId) {
        ClubUser clubUser = validateClubUser(clubId, userId);
        if (clubUser.isNotManager()) throw new IllegalStateException("관리자만 접근 가능합니다.");
    }

}
