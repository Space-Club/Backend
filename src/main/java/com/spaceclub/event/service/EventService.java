package com.spaceclub.event.service;

import com.spaceclub.club.domain.Club;
import com.spaceclub.club.repository.ClubRepository;
import com.spaceclub.club.util.ClubUserValidator;
import com.spaceclub.event.domain.Event;
import com.spaceclub.event.domain.EventCategory;
import com.spaceclub.event.repository.EventRepository;
import com.spaceclub.event.repository.EventUserRepository;
import com.spaceclub.event.service.util.EventValidator;
import com.spaceclub.event.service.vo.ClubEventOverviewGetInfo;
import com.spaceclub.event.service.vo.EventCreateInfo;
import com.spaceclub.event.service.vo.EventGetInfo;
import com.spaceclub.event.service.vo.UserBookmarkedEventGetInfo;
import com.spaceclub.global.config.s3.S3Properties;
import com.spaceclub.global.s3.S3Folder;
import com.spaceclub.global.s3.S3ImageUploader;
import com.spaceclub.user.service.UserProvider;
import com.spaceclub.user.service.vo.UserProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

import static com.spaceclub.club.ClubExceptionMessage.CLUB_NOT_FOUND;
import static com.spaceclub.event.EventExceptionMessage.EVENT_CATEGORY_NOT_ALLOWED;
import static com.spaceclub.event.EventExceptionMessage.EVENT_NOT_FOUND;
import static com.spaceclub.event.EventExceptionMessage.POSTER_IMAGE_NOT_NULL;
import static com.spaceclub.event.domain.EventCategory.CLUB;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EventService implements EventProvider {

    private final EventRepository eventRepository;

    private final ClubRepository clubRepository;

    private final EventUserRepository eventUserRepository;

    private final ClubUserValidator clubUserValidator;

    private final UserProvider userProvider;

    private final S3ImageUploader imageUploader;

    private final S3Properties s3Properties;

    @Transactional
    public Long create(EventCreateInfo vo) {
        Event event = vo.event();
        Long clubId = vo.clubId();
        Long userId = vo.userId();
        MultipartFile posterImage = vo.posterImage();

        clubUserValidator.validateClubManager(clubId, userId);
        EventValidator.validateEvent(event);

        Club club = clubRepository.findById(clubId)
                .orElseThrow(() -> new IllegalStateException(CLUB_NOT_FOUND.toString()));

        boolean posterImageNotNull = event.getCategory() != CLUB && posterImage == null;
        if (posterImageNotNull) throw new IllegalStateException(POSTER_IMAGE_NOT_NULL.toString());

        if (posterImage != null) {
            String posterImageName = imageUploader.upload(posterImage, S3Folder.EVENT_POSTER);
            event = event.registerPosterImage(posterImageName);
        }

        Event registeredEvent = event.registerClubAndUser(club, userId);

        Event savedEvent = eventRepository.save(registeredEvent);
        return savedEvent.getId();
    }

    @Transactional
    public void update(Event event, Long userId, MultipartFile posterImage) {
        clubUserValidator.validateClubManager(event.getClubId(), userId);
        EventValidator.validateEvent(event);

        Event existEvent = eventRepository.findById(event.getId())
                .orElseThrow(() -> new IllegalArgumentException(EVENT_NOT_FOUND.toString()));

        Event updatedEvent = processPosterImage(event, existEvent, posterImage);

        eventRepository.save(updatedEvent);
    }

    private Event processPosterImage(Event newEvent, Event existEvent, MultipartFile posterImage) {
        String posterImageName = (posterImage != null) ?
                imageUploader.upload(posterImage, S3Folder.EVENT_POSTER) :
                existEvent.getPosterImageName();

        Event updatedEvent = newEvent.registerPosterImage(posterImageName);
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
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException(EVENT_NOT_FOUND.toString()));
        clubUserValidator.validateClubManager(event.getClubId(), userId);

        eventRepository.deleteById(eventId);
    }

    public EventGetInfo get(Long eventId, Long userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException(EVENT_NOT_FOUND.toString()));

        if (event.getCategory() == CLUB)
            clubUserValidator.validateClubMember(event.getClubId(), userId);

        boolean hasAlreadyApplied = eventUserRepository.existsByEventIdAndUserId(eventId, userId);
        int applicants = eventUserRepository.countByEvent_Id(eventId);

        return new EventGetInfo(event, hasAlreadyApplied, applicants);
    }

    @Override
    public Page<ClubEventOverviewGetInfo> getByClubId(Long clubId, Pageable pageable) {
        Page<Event> events = eventRepository.findByClub_Id(clubId, pageable);

        return events.map(event -> {
            UserProfile profile = userProvider.getProfile(event.getUserId());
            return ClubEventOverviewGetInfo.from(event, profile, s3Properties.url());
        });
    }

    @Override
    public Page<UserBookmarkedEventGetInfo> findAllBookmarkedEventPages(Long userId, Pageable pageable) {
        Page<Event> events = eventRepository.findAllBookmarkedEventPages(userId, pageable);

        return events.map(event -> UserBookmarkedEventGetInfo.from(event, s3Properties.url()));
    }

    public List<Event> getBanner(LocalDateTime now, int limit) {
        PageRequest pageable = PageRequest.of(0, limit, Sort.by("formInfo.formCloseDateTime").ascending());

        return eventRepository.findAllByFormCloseDateTimeGreaterThan(now, pageable).getContent();
    }

    @Override
    public Event getById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException(EVENT_NOT_FOUND.toString()));
    }

    @Override
    public Event save(Event event) {
        return eventRepository.save(event);
    }

}
