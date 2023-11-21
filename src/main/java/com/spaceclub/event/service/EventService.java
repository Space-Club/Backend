package com.spaceclub.event.service;

import com.spaceclub.club.domain.Club;
import com.spaceclub.club.domain.ClubUser;
import com.spaceclub.club.repository.ClubRepository;
import com.spaceclub.club.repository.ClubUserRepository;
import com.spaceclub.event.domain.ApplicationStatus;
import com.spaceclub.event.domain.Event;
import com.spaceclub.event.domain.EventCategory;
import com.spaceclub.event.domain.EventUser;
import com.spaceclub.event.repository.EventRepository;
import com.spaceclub.event.repository.EventUserRepository;
import com.spaceclub.event.service.vo.EventApplicationCreateInfo;
import com.spaceclub.form.domain.FormOption;
import com.spaceclub.form.domain.FormOptionUser;
import com.spaceclub.form.repository.FormOptionRepository;
import com.spaceclub.form.repository.FormOptionUserRepository;
import com.spaceclub.user.domain.User;
import com.spaceclub.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.spaceclub.event.domain.ApplicationStatus.PENDING;
import static com.spaceclub.event.domain.EventCategory.CLUB;
import static com.spaceclub.global.ExceptionCode.CLUB_NOT_FOUND;
import static com.spaceclub.global.ExceptionCode.EVENT_ALREADY_APPLIED;
import static com.spaceclub.global.ExceptionCode.EVENT_CATEGORY_NOT_ALLOWED;
import static com.spaceclub.global.ExceptionCode.EVENT_NOT_APPLIED;
import static com.spaceclub.global.ExceptionCode.EVENT_NOT_FOUND;
import static com.spaceclub.global.ExceptionCode.EVENT_TICKET_NOT_MANAGED;
import static com.spaceclub.global.ExceptionCode.EXCEED_TICKET_COUNT;
import static com.spaceclub.global.ExceptionCode.FORM_OPTION_NOT_FOUND;
import static com.spaceclub.global.ExceptionCode.NOT_CLUB_MEMBER;
import static com.spaceclub.global.ExceptionCode.TICKET_COUNT_REQUIRED;
import static com.spaceclub.global.ExceptionCode.UNAUTHORIZED;
import static com.spaceclub.global.ExceptionCode.USER_NOT_FOUND;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    private final ClubRepository clubRepository;

    private final ClubUserRepository clubUserRepository;

    private final UserRepository userRepository;

    private final EventUserRepository eventUserRepository;

    private final FormOptionRepository formOptionRepository;

    private final FormOptionUserRepository formOptionUserRepository;


    @Transactional
    public Long create(Event event, Long clubId, Long userId) {
        Club club = validateClubManager(clubId, userId);
        Event registeredEvent = event.registerClub(club);

        return eventRepository.save(registeredEvent).getId();
    }

    @Transactional
    public void update(Event event, Long userId) {
        validateClubManager(event.getClubId(), userId);

        eventRepository.save(event);
    }

    public Page<Event> getAllEvents(EventCategory eventCategory, Pageable pageable) {
        if (eventCategory == CLUB) {
            throw new IllegalArgumentException(EVENT_CATEGORY_NOT_ALLOWED.toString());
        }
        return eventRepository.findAllByCategory(eventCategory, pageable);
    }

    public Page<Event> getSearchEvents(String keyword, Pageable pageable) {
        return eventRepository.findByEventInfo_TitleContainsIgnoreCase(keyword, pageable);
    }

    @Transactional
    public void delete(Long eventId, Long userId) {
        Event event = validateEvent(eventId);
        validateClubManager(event.getClubId(), userId);

        eventRepository.deleteById(eventId);
    }

    @Transactional
    public void createApplicationForm(EventApplicationCreateInfo info) {
        User user = userRepository.findById(info.userId()).orElseThrow(() -> new IllegalStateException(USER_NOT_FOUND.toString()));
        Event event = validateEvent(info.eventId());

        validateEventTicketCount(event.getMaxTicketCount(), info.ticketCount());
        if (eventUserRepository.existsByEventIdAndUserId(info.eventId(), info.userId()))
            throw new IllegalArgumentException(EVENT_ALREADY_APPLIED.toString());

        for (FormOptionUser formOptionUser : info.formOptionUsers()) {
            FormOption formOption = formOptionRepository.findById(formOptionUser.getFormOptionId())
                    .orElseThrow(() -> new IllegalStateException(FORM_OPTION_NOT_FOUND.toString()));

            FormOptionUser registeredFormOptionUser = formOptionUser.registerFormOptionAndUser(formOption, user);
            formOptionUserRepository.save(registeredFormOptionUser);
            formOption.addFormOptionUser(registeredFormOptionUser);
            formOptionRepository.save(formOption);
        }

        EventUser newEventUser = EventUser.builder()
                .user(user)
                .event(event)
                .status(PENDING)
                .ticketCount(info.ticketCount())
                .build();

        eventUserRepository.save(newEventUser);
    }

    private void validateEventTicketCount(Integer maxTicketCount, Integer ticketCount) {
        boolean eventTicketNotManaged = maxTicketCount == null && ticketCount != null;
        boolean ticketCountRequired = maxTicketCount != null && ticketCount == null;
        boolean exceedTicketCount = ticketCount != null &&
                maxTicketCount != null &&
                maxTicketCount < ticketCount;

        if (eventTicketNotManaged) throw new IllegalArgumentException(EVENT_TICKET_NOT_MANAGED.toString());
        if (ticketCountRequired) throw new IllegalArgumentException(TICKET_COUNT_REQUIRED.toString());
        if (exceedTicketCount)
            throw new IllegalArgumentException(EXCEED_TICKET_COUNT.toString());
    }

    public Event get(Long eventId) {
        return validateEvent(eventId);
    }

    @Transactional
    public ApplicationStatus cancelEvent(Long eventId, Long userId) {
        Event event = validateEvent(eventId);

        EventUser eventUser = eventUserRepository.findByEventIdAndUserId(eventId, userId)
                .orElseThrow(() -> new IllegalArgumentException(EVENT_NOT_APPLIED.toString()));

        EventUser updateEventUser = eventUser.setStatusByManaged(event.isFormManaged());

        eventUserRepository.save(updateEventUser);

        return updateEventUser.getStatus();
    }

    private Club validateClubManager(Long clubId, Long userId) {
        if (!clubRepository.existsById(clubId)) throw new IllegalStateException(CLUB_NOT_FOUND.toString());
        if (!userRepository.existsById(userId)) throw new IllegalStateException(USER_NOT_FOUND.toString());

        ClubUser clubUser = clubUserRepository.findByClub_IdAndUserId(clubId, userId)
                .orElseThrow(() -> new IllegalStateException(NOT_CLUB_MEMBER.toString()));
        if (clubUser.isNotManager()) throw new IllegalStateException(UNAUTHORIZED.toString());

        return clubUser.getClub();
    }

    private Event validateEvent(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException(EVENT_NOT_FOUND.toString()));
    }

    public Page<Event> getByClubId(Long clubId, Pageable pageable) {
        return eventRepository.findByClub_Id(clubId, pageable);
    }

    public List<Event> getSchedulesByClubId(Long clubId) {
        return eventRepository.findAllByClub_IdAndCategory(clubId, CLUB);
    }

}
