package com.spaceclub.event.service;

import com.spaceclub.event.service.vo.EventPageInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserEventProvider {

    Page<EventPageInfo> findAllEventPages(Long userId, Pageable pageable);

    int getTicketCount(Long eventId, Long userId);

}
