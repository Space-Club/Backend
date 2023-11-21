package com.spaceclub.club.service;

import com.spaceclub.club.repository.ClubUserRepository;
import com.spaceclub.event.service.EventProvider;
import com.spaceclub.event.service.vo.EventGetInfo;
import com.spaceclub.event.service.vo.SchedulesGetInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.spaceclub.global.ExceptionCode.NOT_CLUB_MEMBER;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ClubEventService {

    private final EventProvider eventProvider;

    private final ClubUserRepository clubUserRepository;

    public Page<EventGetInfo> getClubEvents(Long clubId, Pageable pageable, Long userId) {
        checkClubMember(clubId, userId);

        return eventProvider.getByClubId(clubId, pageable);
    }

    public List<SchedulesGetInfo> getClubSchedules(Long clubId, Long userId) {
        checkClubMember(clubId, userId);

        return eventProvider.getSchedulesByClubId(clubId);
    }

    private void checkClubMember(Long clubId, Long userId) {
        if (!clubUserRepository.existsByClub_IdAndUserId(clubId, userId))
            throw new IllegalArgumentException(NOT_CLUB_MEMBER.toString());
    }

}
