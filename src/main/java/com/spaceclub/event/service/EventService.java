package com.spaceclub.event.service;

import com.spaceclub.club.domain.Club;
import com.spaceclub.club.repository.ClubRepository;
import com.spaceclub.event.domain.Event;
import com.spaceclub.event.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    private final ClubRepository clubRepository;

    public Long create(Event event, Long clubId) {
        Club club = clubRepository.findById(clubId).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 클럽입니다.")
        );
        Event registeredEvent = event.registerClub(club);

        return eventRepository.save(registeredEvent).getId();
    }


    public Page<Event> getAll(Pageable pageable) {
        return eventRepository.findAll(pageable);
    }

}
