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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static com.spaceclub.club.ClubExceptionMessage.CLUB_NOT_FOUND;
import static com.spaceclub.event.EventExceptionMessage.EVENT_CATEGORY_NOT_ALLOWED;
import static com.spaceclub.event.EventExceptionMessage.EVENT_NOT_FOUND;
import static com.spaceclub.event.EventExceptionMessage.POSTER_IMAGE_NOT_NULL;
import static com.spaceclub.event.domain.EventCategory.CLUB;
import static org.springframework.data.domain.Sort.Direction.ASC;

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
        Event existEvent = eventRepository.findById(event.getId())
                .orElseThrow(() -> new IllegalArgumentException(EVENT_NOT_FOUND.toString()));

        EventValidator.validateEvent(event);
        clubUserValidator.validateClubManager(existEvent.getClubId(), userId);

        Event updatedEvent = processPosterImage(event, existEvent, posterImage);

        eventRepository.save(updatedEvent);
    }

    private Event processPosterImage(Event newEvent, Event existEvent, MultipartFile posterImage) {
        String posterImageName;

        if (posterImage == null) posterImageName = existEvent.getPosterImageName();
        else posterImageName = imageUploader.upload(posterImage, S3Folder.EVENT_POSTER);

        int participants = existEvent.getParticipants();

        Event updatedEvent = newEvent.registerPosterImage(posterImageName);
        updatedEvent = updatedEvent.registerParticipants(participants);
        
        return existEvent.update(updatedEvent);
    }

    public Page<Event> getAll(EventCategory category, Pageable pageable) {
        Comparator<Event> comparator = eventComparator(category);

        Page<Event> eventPage = eventRepository.findAllByCategory(category, pageable);
        List<Event> events = new ArrayList<>(eventPage.getContent());
        events.sort(comparator);

        return new PageImpl<>(events, pageable, eventPage.getTotalElements());
    }

    private Comparator<Event> eventComparator(EventCategory category) {
        return switch (category) {
            case SHOW, PROMOTION -> Comparator
                    .comparing(Event::isEventEnded)
                    .thenComparing(Event::getStartDateTime);
            case RECRUITMENT -> Comparator
                    .comparing(Event::isEventEnded)
                    .thenComparing(Event::getFormCloseDateTime);
            default -> throw new IllegalArgumentException(EVENT_CATEGORY_NOT_ALLOWED.toString());
        };
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

    @Transactional
    public void deleteClubEvents(Long clubId) {
        List<Event> events = eventRepository.findByClub_Id(clubId);

        eventRepository.deleteAll(events);
    }

    public EventGetInfo get(Long eventId, Long userId) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new IllegalArgumentException(EVENT_NOT_FOUND.toString()));

        if (event.getCategory() == CLUB)
            clubUserValidator.validateClubMember(event.getClubId(), userId);

        boolean hasAlreadyApplied = eventUserRepository.existsByEventIdAndUserId(eventId, userId);

        return new EventGetInfo(event, hasAlreadyApplied);
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

    @Override
    public void minusParticipants(Event event, int ticketCount) {
        int remainCapacity = event.getParticipants() - ticketCount;
        Event updateEvent = event.registerParticipants(remainCapacity);

        eventRepository.save(updateEvent);
    }

    @Override
    public void update(Event event) {
        eventRepository.save(event);
    }

    public List<Event> getBanner(LocalDateTime now, int limit) {
        PageRequest pageable = PageRequest.of(0, limit, Sort.by(ASC, "formInfo.formCloseDateTime"));

        return eventRepository.findAllByCategoryNotAndFormInfo_FormCloseDateTimeGreaterThanAndFormInfo_FormOpenDateTimeLessThan(CLUB, now, now, pageable).getContent();
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
