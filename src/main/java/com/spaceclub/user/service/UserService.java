package com.spaceclub.user.service;

import com.spaceclub.event.domain.Event;
import com.spaceclub.event.repository.EventUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final EventUserRepository eventUserRepository;

    public Page<Event> findAllEventPages(Long userId, Pageable pageable) {
        return eventUserRepository.findAllByUserId(userId, pageable);
    }

    public String findEventStatus(Long userId, Event event){
        return eventUserRepository.findEventStatusByUserId(userId, event);
    }

}


