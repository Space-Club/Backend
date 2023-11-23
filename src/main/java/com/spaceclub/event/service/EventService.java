package com.spaceclub.event.service;

import com.spaceclub.club.domain.Club;
import com.spaceclub.event.domain.Event;
import com.spaceclub.event.domain.EventCategory;
import com.spaceclub.event.repository.EventRepository;
import com.spaceclub.event.service.vo.EventCreateInfo;
import com.spaceclub.event.service.vo.EventGetInfo;
import com.spaceclub.event.service.vo.SchedulesGetInfo;
import com.spaceclub.form.service.ClubUserValidator;
import com.spaceclub.global.S3ImageUploader;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.spaceclub.event.domain.EventCategory.CLUB;
import static com.spaceclub.global.ExceptionCode.EVENT_CATEGORY_NOT_ALLOWED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EventService implements EventProvider {

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

    public Page<Event> findAllBookmarkedEventPages(Long userId, Pageable pageable) {
        return eventRepository.findAllBookmarkedEventPages(userId, pageable);
    }

}
