package com.spaceclub.event.service;

import com.spaceclub.event.domain.Event;
import com.spaceclub.event.domain.EventUser;
import com.spaceclub.event.repository.EventUserRepository;
import com.spaceclub.event.service.vo.EventPageInfo;
import com.spaceclub.global.config.s3.S3Properties;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import static java.util.stream.Collectors.toMap;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserEventService implements UserEventProvider {

    private final EventUserRepository eventUserRepository;

    private final S3Properties s3Properties;

    @Override
    public Page<EventPageInfo> findAllEventPages(Long userId, Pageable pageable) {
        Page<Event> eventPages = eventUserRepository.findAllByUserId(userId, pageable);

        List<Long> eventIds = eventPages.stream()
                .map(Event::getId)
                .toList();

        Map<Long, EventUser> eventUsers = eventUserRepository.findAllByUserIdAndEvent_IdIn(userId, eventIds).stream()
                .collect(toMap(EventUser::getEventId, Function.identity()));

        List<EventPageInfo> eventPageInfos = eventPages.getContent().stream()
                .map(event -> EventPageInfo.from(event, eventUsers.get(event.getId()), s3Properties.url()))
                .toList();

        return new PageImpl<>(eventPageInfos, eventPages.getPageable(), eventPages.getTotalElements());
    }

}
