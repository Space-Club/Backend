package com.spaceclub.event.service;

import com.spaceclub.event.service.vo.ClubEventOverviewGetInfo;
import com.spaceclub.event.service.vo.UserBookmarkedEventGetInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EventProvider {

    Page<ClubEventOverviewGetInfo> getByClubId(Long clubId, Pageable pageable);

    Page<UserBookmarkedEventGetInfo> findAllBookmarkedEventPages(Long userId, Pageable pageable);

}
