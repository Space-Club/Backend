package com.spaceclub.event.service;

import com.spaceclub.club.domain.Club;
import com.spaceclub.club.repository.ClubRepository;
import com.spaceclub.event.domain.ApplicationStatus;
import com.spaceclub.event.domain.Event;
import com.spaceclub.event.domain.EventUser;
import com.spaceclub.event.repository.EventRepository;
import com.spaceclub.event.repository.EventUserRepository;
import com.spaceclub.user.domain.User;
import com.spaceclub.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.spaceclub.event.domain.ApplicationStatus.*;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    private final ClubRepository clubRepository;

    private final UserRepository userRepository;

    private final EventUserRepository eventUserRepository;

    @Transactional
    public Long create(Event event, Long clubId, Long userId) {
        Club club = clubRepository.findById(clubId).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 클럽입니다.")
        );
        Event registeredEvent = event.registerClub(club);

        return eventRepository.save(registeredEvent).getId();
    }

    @Transactional
    public void update(Event event, Long userId) {
        eventRepository.findById(event.getId()).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 행사입니다.")
        );

        eventRepository.save(event);
    }

    public Page<Event> getAll(Pageable pageable) {
        return eventRepository.findAll(pageable);
    }

    public Page<Event> getSearchEvents(String keyword, Pageable pageable, Long userId) {
        return eventRepository.findByEventInfo_TitleContainsIgnoreCase(keyword, pageable);
    }

    @Transactional
    public void delete(Long eventId, Long userId) {
        eventRepository.deleteById(eventId);
    }

    @Transactional
    public void applyEvent(Long eventId, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 회원입니다.")
        );

        Event event = eventRepository.findById(eventId).orElseThrow(
                () -> new IllegalArgumentException("존재하지 않는 행사입니다.")
        );
        validateIfEventUserExists(eventId, userId);

        EventUser newEventUser = EventUser.builder()
                .user(user)
                .event(event)
                .status(PENDING)
                .build();

        eventUserRepository.save(newEventUser);
    }

    private void validateIfEventUserExists(Long eventId, Long userId) {
        eventUserRepository.findByEventIdAndUserId(eventId, userId)
                .ifPresent(eventUser -> {
                    throw new IllegalArgumentException("이미 신청한 행사입니다.");
                });
    }

    public Event get(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 행사입니다."));
    }

    @Transactional
    public ApplicationStatus cancelEvent(Long eventId, Long userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 행사입니다."));

        EventUser eventUser = eventUserRepository.findByEventIdAndUserId(eventId, userId)
                .orElseThrow(() -> new IllegalArgumentException("신청한 이력이 없는 행사입니다."));

        EventUser updateEventUser = eventUser.setStatusByManaged(event.isFormManaged());

        eventUserRepository.save(updateEventUser);

        return updateEventUser.getStatus();
    }

}
