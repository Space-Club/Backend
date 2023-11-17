package com.spaceclub.event.service;

import com.spaceclub.club.domain.Club;
import com.spaceclub.club.repository.ClubRepository;
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

import static com.spaceclub.event.domain.ApplicationStatus.PENDING;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    private final ClubRepository clubRepository;

    private final UserRepository userRepository;

    private final EventUserRepository eventUserRepository;

    private final FormOptionRepository formOptionRepository;

    private final FormOptionUserRepository formOptionUserRepository;


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

    public Page<Event> getAllEvents(EventCategory eventCategory, Pageable pageable) {
        return eventRepository.findAllByCategory(eventCategory, pageable);
    }

    public Page<Event> getSearchEvents(String keyword, Pageable pageable, Long userId) {
        return eventRepository.findByEventInfo_TitleContainsIgnoreCase(keyword, pageable);
    }

    @Transactional
    public void delete(Long eventId, Long userId) {
        eventRepository.deleteById(eventId);
    }

    @Transactional
    public void createApplicationForm(EventApplicationCreateInfo info) {
        User user = userRepository.findById(info.userId()).orElseThrow(() -> new IllegalStateException("존재하지 않는 유저입니다."));
        Event event = eventRepository.findById(info.eventId()).orElseThrow(() -> new IllegalStateException("존재하지 않는 행사입니다."));

        validateEventTicketCount(event.getMaxTicketCount(), info.ticketCount());
        if (eventUserRepository.existsByEventIdAndUserId(info.eventId(), info.userId())) throw new IllegalArgumentException("이미 신청한 행사입니다.");

        for (FormOptionUser formOptionUser : info.formOptionUsers()) {
            FormOption formOption = formOptionRepository.findById(formOptionUser.getFormOptionId())
                    .orElseThrow(() -> new IllegalStateException("존재하지 않는 폼 옵션 입니다."));

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
        if (maxTicketCount == null && ticketCount != null) throw new IllegalArgumentException("행사 티켓을 관리하지 않는 행사입니다.");
        if (maxTicketCount != null && ticketCount == null) throw new IllegalArgumentException("행사 티켓 매수는 필수입니다.");
        if (ticketCount != null && maxTicketCount < ticketCount)
            throw new IllegalArgumentException("인 당 티켓 예매 수를 초과하였습니다.");
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
