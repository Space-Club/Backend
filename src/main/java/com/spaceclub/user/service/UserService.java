package com.spaceclub.user.service;

import com.spaceclub.event.domain.Event;
import com.spaceclub.event.repository.EventUserRepository;
import com.spaceclub.user.domain.User;
import com.spaceclub.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserService {

    private final EventUserRepository eventUserRepository;
    private final UserRepository userRepository;

    public Page<Event> findAllEventPages(Long userId, Pageable pageable) {
        return eventUserRepository.findAllByUserId(userId, pageable);
    }

    public String findEventStatus(Long userId, Event event){
        return eventUserRepository.findEventStatusByUserId(userId, event);
    }

    public boolean isNewMember(User user) {
        Long userId = user.getId();
        return userRepository.findById(userId)
                .orElseThrow()
                .isNewMember();
    }

    @Transactional
    public void save(User user) {
        userRepository.save(user);
    }

}


