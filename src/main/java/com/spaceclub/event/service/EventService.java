package com.spaceclub.event.service;

import com.spaceclub.club.domain.Club;
import com.spaceclub.club.repository.ClubRepository;
import com.spaceclub.event.domain.ApplicationStatus;
import com.spaceclub.event.domain.Event;
import com.spaceclub.event.domain.EventUser;
import com.spaceclub.event.repository.EventRepository;
import com.spaceclub.event.repository.EventUserRepository;
import com.spaceclub.event.service.vo.EventBookmarkInfo;
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
public class EventService {

    private final EventRepository eventRepository;

    private final ClubRepository clubRepository;

    private final UserRepository userRepository;

    private final EventUserRepository eventUserRepository;

    @Transactional
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

    @Transactional
    public void applyEvent(Long eventId, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 회원입니다.")
        );

        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 행사입니다.")
        );

        EventUser newEventUser = EventUser.builder()
                .user(user)
                .event(event)
                .status(ApplicationStatus.PENDING)
                .build();

        eventUserRepository.save(newEventUser);
    }

    public Event get(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 행사입니다."));
    }

    @Transactional
    public void cancelEvent(Long eventId, Long userId) {
    }

    @Transactional
    public void changeBookmarkStatus(EventBookmarkInfo eventBookmarkInfo) {
        Event event = eventRepository.findById(eventBookmarkInfo.eventId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 행사입니다."));
        User user = userRepository.findById(eventBookmarkInfo.userId())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 유저입니다."));

        EventUser bookmarkedEventUser = eventUserRepository.findByUserAndEvent(user, event)
                .orElseThrow(() -> new IllegalArgumentException("연관되어 있지 않는 유저와 행사입니다."))
                .bookmark(eventBookmarkInfo.bookmarkStatus());

        eventUserRepository.save(bookmarkedEventUser);
    }

}
