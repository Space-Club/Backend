package com.spaceclub.event.service;

import com.spaceclub.club.domain.Club;
import com.spaceclub.club.repository.ClubRepository;
import com.spaceclub.club.service.ClubUserValidator;
import com.spaceclub.event.domain.Event;
import com.spaceclub.event.domain.EventCategory;
import com.spaceclub.event.repository.EventRepository;
import com.spaceclub.event.repository.EventUserRepository;
import com.spaceclub.event.service.vo.EventCreateInfo;
import com.spaceclub.event.service.vo.EventGetInfo;
import com.spaceclub.event.service.vo.SchedulesGetInfo;
import com.spaceclub.global.config.s3.S3ImageUploader;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.spaceclub.club.ClubExceptionMessage.CLUB_NOT_FOUND;
import static com.spaceclub.event.EventExceptionMessage.EVENT_CATEGORY_NOT_ALLOWED;
import static com.spaceclub.event.domain.EventCategory.CLUB;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EventService implements EventProvider {

    private final EventRepository eventRepository;

    private final ClubRepository clubRepository;

    private final EventUserRepository eventUserRepository;

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

        clubUserValidator.validateClubManager(createInfo.clubId(), createInfo.userId());
        Club club = clubRepository.findById(createInfo.clubId())
                .orElseThrow(() -> new IllegalStateException(CLUB_NOT_FOUND.toString()));

        Event registeredEvent = event.registerClub(club);

        return eventRepository.save(registeredEvent).getId();
    }

    @Transactional
    public void update(Event event, Long userId, MultipartFile posterImage) {
        Event existEvent = eventValidator.validateEvent(event.getId());
        clubUserValidator.validateClubManager(existEvent.getClubId(), userId);

        Event updatedEvent = processPosterImage(event, existEvent, posterImage);

        eventRepository.save(updatedEvent);
    }

    private Event processPosterImage(Event originalEvent, Event existEvent, MultipartFile posterImage) {
        String posterImageName = (posterImage != null) ? imageUploader.uploadPosterImage(posterImage) : existEvent.getPosterImageName();
        Event updatedEvent = originalEvent.registerPosterImage(posterImageName);
        return existEvent.update(updatedEvent);
    }

    public Page<Event> getAll(EventCategory eventCategory, Pageable pageable) {
        if (CLUB.equals(eventCategory)) {
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

    public Event get(Long eventId, Long userId) {
        Event event = eventValidator.validateEvent(eventId);

        if (event.getCategory() == CLUB)
            clubUserValidator.validateClubMember(event.getClubId(), userId);

        return event;
    }

    @Override
    public Page<EventGetInfo> getByClubId(Long clubId, Pageable pageable) {
        Page<Event> events = eventRepository.findByClub_Id(clubId, pageable);

        return events.map(EventGetInfo::from);
    }

    @Override
    public List<SchedulesGetInfo> getSchedulesByClubId(Long clubId) {
        List<Event> events = eventRepository.findAllByClub_IdAndCategory(clubId, CLUB);

        return events.stream()
                .map(SchedulesGetInfo::from)
                .toList();
    }

    @Override
    public Page<EventGetInfo> findAllBookmarkedEventPages(Long userId, Pageable pageable) {
        Page<Event> events = eventRepository.findAllBookmarkedEventPages(userId, pageable);

        return events.map(EventGetInfo::from);
    }

    public int countApplicants(Long eventId) {
        return eventUserRepository.countByEvent_Id(eventId);
    }

}
