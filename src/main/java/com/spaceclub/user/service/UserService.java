package com.spaceclub.user.service;

import com.spaceclub.event.domain.Event;
import com.spaceclub.event.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final EventRepository eventRepository;

    public Page<Event> findAllEventPages(Long userId, Pageable pageable) {
//        return eventRepository.findAllByUserId(userId, pageable);
        return null;
    }

    public String findEventStatus(Long userId, Event event) {
//        return eventRepository.findEventStatusByUserId(userId, event);
        return null;
    }

}


