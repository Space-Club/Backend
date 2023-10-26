package com.spaceclub.user.service;

import com.spaceclub.event.domain.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface UserService {

    Page<Event> findAllEventPages(Long userId, Pageable pageable);

<<<<<<< HEAD
=======

>>>>>>> 99781ac (feat: user controller 및 mock User service 생성)
}
