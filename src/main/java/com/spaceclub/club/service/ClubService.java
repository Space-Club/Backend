package com.spaceclub.club.service;

import com.spaceclub.club.domain.Club;
import com.spaceclub.club.repository.ClubRepository;
import com.spaceclub.event.domain.Category;
import com.spaceclub.event.domain.Event;
import com.spaceclub.event.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class ClubService {

    private final ClubRepository repository;

    private final EventRepository eventRepository;

    public Club createClub(Club club) {
        return repository.save(club);
    }

    public Club getClub(Long clubId) {
        return repository.findById(clubId)
                .orElseThrow(() -> new IllegalArgumentException("해당하는 클럽이 없습니다"));
    }

    public void deleteClub(Long clubId) {
        repository.deleteById(clubId);
    }

    public Page<Event> getClubEvents(Long clubId, Pageable pageable) {
        return eventRepository.findByClub_Id(clubId, pageable);
    }

}
