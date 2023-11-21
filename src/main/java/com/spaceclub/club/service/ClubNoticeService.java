package com.spaceclub.club.service;

import com.spaceclub.club.domain.Club;
import com.spaceclub.club.domain.ClubNotice;
import com.spaceclub.club.domain.ClubUser;
import com.spaceclub.club.repository.ClubNoticeRepository;
import com.spaceclub.club.repository.ClubRepository;
import com.spaceclub.club.repository.ClubUserRepository;
import com.spaceclub.club.service.vo.ClubNoticeDelete;
import com.spaceclub.club.service.vo.ClubNoticeUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.spaceclub.global.ExceptionCode.CLUB_NOT_FOUND;
import static com.spaceclub.global.ExceptionCode.NOTICE_NOT_FOUND;
import static com.spaceclub.global.ExceptionCode.NOT_CLUB_MEMBER;
import static com.spaceclub.global.ExceptionCode.UNAUTHORIZED;

@Service
@Transactional
@RequiredArgsConstructor
public class ClubNoticeService {

    private final ClubRepository clubRepository;

    private final ClubUserRepository clubUserRepository;

    private final ClubNoticeRepository clubNoticeRepository;

    public void createNotice(String notice, Long clubId, Long userId) {
        ClubUser clubUser = clubUserRepository.findByClub_IdAndUserId(clubId, userId)
                .orElseThrow(() -> new IllegalArgumentException(NOT_CLUB_MEMBER.toString()));

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
        if (!clubUserRepository.existsByClub_IdAndUserId(clubId, userId))
            throw new IllegalArgumentException(NOT_CLUB_MEMBER.toString());

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

        ClubUser clubUser = clubUserRepository.findByClub_IdAndUserId(clubId, userId)
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

        ClubUser clubUser = clubUserRepository.findByClub_IdAndUserId(clubId, userId)
                .orElseThrow(() -> new IllegalArgumentException(NOT_CLUB_MEMBER.toString()));

        if (clubUser.isNotManager()) throw new IllegalStateException(UNAUTHORIZED.toString());

        clubNoticeRepository.deleteById(noticeId);
    }

}
