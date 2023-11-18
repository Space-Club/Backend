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

import static com.spaceclub.event.domain.ApplicationStatus.PENDING;

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
        if (eventCategory == EventCategory.CLUB) {
            throw new IllegalArgumentException("클럽을 제외한 카테고리의 행사만 조회 가능합니다.");
        }
        return eventRepository.findAllByCategory(eventCategory, pageable);
    }

    public Page<Event> getSearchEvents(String keyword, Pageable pageable, Long userId) {
        if (!userRepository.existsById(userId)) throw new IllegalStateException("존재하지 않는 유저입니다.");

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
        User user = userRepository.findById(info.userId()).orElseThrow(() -> new IllegalStateException("존재하지 않는 유저입니다."));
        Event event = validateEvent(info.eventId());

        validateEventTicketCount(event.getMaxTicketCount(), info.ticketCount());
        if (eventUserRepository.existsByEventIdAndUserId(info.eventId(), info.userId()))
            throw new IllegalArgumentException("이미 신청한 행사입니다.");

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
        return validateEvent(eventId);
    }

    @Transactional
    public ApplicationStatus cancelEvent(Long eventId, Long userId) {
        Event event = validateEvent(eventId);

        EventUser eventUser = eventUserRepository.findByEventIdAndUserId(eventId, userId)
                .orElseThrow(() -> new IllegalArgumentException("신청한 이력이 없는 행사입니다."));

        EventUser updateEventUser = eventUser.setStatusByManaged(event.isFormManaged());

        eventUserRepository.save(updateEventUser);

        return updateEventUser.getStatus();
    }

    private Club validateClubManager(Long clubId, Long userId) {
        if (!clubRepository.existsById(clubId)) throw new IllegalStateException("존재하지 않는 클럽입니다.");
        if (!userRepository.existsById(userId)) throw new IllegalStateException("존재하지 않는 유저입니다.");

        ClubUser clubUser = clubUserRepository.findByClub_IdAndUser_Id(clubId, userId)
                .orElseThrow(() -> new IllegalStateException("클럽의 멤버가 아닙니다."));
        if (clubUser.isNotManager()) throw new IllegalStateException("관리자만 접근 가능합니다.");

        return clubUser.getClub();
    }

    private Event validateEvent(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 행사입니다."));
    }

}
