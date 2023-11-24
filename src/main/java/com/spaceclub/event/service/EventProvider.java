package com.spaceclub.event.service;

import com.spaceclub.event.service.vo.EventGetInfo;
import com.spaceclub.event.service.vo.SchedulesGetInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EventProvider {

    Page<EventGetInfo> getByClubId(Long clubId, Pageable pageable);

    List<SchedulesGetInfo> getSchedulesByClubId(Long clubId);

    Page<EventGetInfo> findAllBookmarkedEventPages(Long userId, Pageable pageable);

}
