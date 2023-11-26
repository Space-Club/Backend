package com.spaceclub.event.service;

import com.spaceclub.event.service.vo.EventGetInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface EventProvider {

    Page<EventGetInfo> getByClubId(Long clubId, Pageable pageable);

    Page<EventGetInfo> findAllBookmarkedEventPages(Long userId, Pageable pageable);

}
