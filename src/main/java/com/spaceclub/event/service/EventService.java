package com.spaceclub.event.service;

import com.spaceclub.club.domain.Club;
import com.spaceclub.event.domain.Event;
import com.spaceclub.event.domain.EventCategory;
import com.spaceclub.event.repository.EventRepository;
import com.spaceclub.event.service.vo.EventCreateInfo;
import com.spaceclub.form.service.ClubUserValidator;
import com.spaceclub.global.S3ImageUploader;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import static com.spaceclub.event.domain.ApplicationStatus.PENDING;
import static com.spaceclub.global.ExceptionCode.CLUB_NOT_FOUND;
import static com.spaceclub.global.ExceptionCode.EVENT_ALREADY_APPLIED;
import static com.spaceclub.global.ExceptionCode.EVENT_CATEGORY_NOT_ALLOWED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;

    private final EventValidator eventValidator;

    private final ClubUserValidator clubUserValidator;

    private final S3ImageUploader imageUploader;


    @Transactional
    public Long create(EventCreateInfo createInfo) {
        MultipartFile posterImage = createInfo.posterImage();
        Event event = createInfo.event();

        if (posterImage != null) {
            String posterImageName = imageUploader.uploadPosterImage(posterImage);
            event = event.registerPosterImage(posterImageName);
        }

        Club club = clubUserValidator.validateClubManager(createInfo.clubId(), createInfo.userId());
        Event registeredEvent = event.registerClub(club);

        return eventRepository.save(registeredEvent).getId();
    }

    @Transactional
    public void update(Event event, Long userId, MultipartFile posterImage) {

        if (posterImage != null) {
            String posterImageName = imageUploader.uploadPosterImage(posterImage);
            event = event.registerPosterImage(posterImageName);
        }

        clubUserValidator.validateClubManager(event.getClubId(), userId);

        eventRepository.save(event);
    }

    public Page<Event> getAll(EventCategory eventCategory, Pageable pageable) {
        if (EventCategory.CLUB.equals(eventCategory)) {
            throw new IllegalArgumentException(EVENT_CATEGORY_NOT_ALLOWED.toString());
        }
        return eventRepository.findAllByCategory(eventCategory, pageable);
    }

    public Page<Event> search(String keyword, Pageable pageable) {
        return eventRepository.findByEventInfo_TitleContainsIgnoreCase(keyword, pageable);
    }

    @Transactional
    public void delete(Long eventId, Long userId) {
        Event event = eventValidator.validateEvent(eventId);
        clubUserValidator.validateClubManager(event.getClubId(), userId);

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
        if (maxTicketCount == null && ticketCount != null) throw new IllegalArgumentException(EVENT_TICKET_NOT_MANAGED.toString());
        if (maxTicketCount != null && ticketCount == null) throw new IllegalArgumentException(TICKET_COUNT_REQUIRED.toString());
        if (ticketCount != null && maxTicketCount < ticketCount)
            throw new IllegalArgumentException(EXCEED_TICKET_COUNT.toString());
    }

    public Event get(Long eventId) {
        return eventValidator.validateEvent(eventId);
    }

    public Page<Event> getByClubId(Long clubId, Pageable pageable) {
        return eventRepository.findByClub_Id(clubId, pageable);
    }

    public List<Event> getSchedulesByClubId(Long clubId) {
        return eventRepository.findAllByClub_IdAndCategory(clubId, CLUB);
    }

}
