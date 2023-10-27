package com.spaceclub.user.service;

import com.spaceclub.event.domain.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    public Page<Event> findAllEventPages(Long userId, Pageable pageable) {
        return null;
    }

}
