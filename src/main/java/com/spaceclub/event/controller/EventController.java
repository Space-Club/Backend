package com.spaceclub.event.controller;

import com.spaceclub.club.domain.Club;
import com.spaceclub.club.service.ClubProvider;
import com.spaceclub.event.controller.dto.EventBannerResponse;
import com.spaceclub.event.controller.dto.EventCreateRequest;
import com.spaceclub.event.controller.dto.EventCreateResponse;
import com.spaceclub.event.controller.dto.EventDetailGetResponse;
import com.spaceclub.event.controller.dto.EventOverviewGetResponse;
import com.spaceclub.event.controller.dto.EventSearchOverviewGetResponse;
import com.spaceclub.event.controller.dto.EventUpdateRequest;
import com.spaceclub.event.domain.Event;
import com.spaceclub.event.domain.EventCategory;
import com.spaceclub.event.service.EventImageService;
import com.spaceclub.event.service.EventService;
import com.spaceclub.event.service.UserEventService;
import com.spaceclub.event.service.vo.EventCreateInfo;
import com.spaceclub.event.service.vo.EventGetInfo;
import com.spaceclub.global.Authenticated;
import com.spaceclub.global.dto.PageResponse;
import com.spaceclub.global.jwt.vo.JwtUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {

    private static final int BANNER_LIMIT = 10;

    private final EventService eventService;

    private final UserEventService userEventService;

    private final ClubProvider clubProvider;

    private final EventImageService eventImageService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<EventCreateResponse> create(
            @RequestPart(required = false) MultipartFile posterImage,
            @RequestPart EventCreateRequest request,
            @RequestPart String category,
            @Authenticated JwtUser jwtUser
    ) {

        EventCategory eventCategory = EventCategory.valueOf(category);

        Event event = request.toEntity(eventCategory);
        Long clubId = request.clubId();

        EventCreateInfo eventInfo = EventCreateInfo.builder()
                .event(event)
                .userId(jwtUser.id())
                .clubId(clubId)
                .posterImage(posterImage)
                .build();

        Club club = clubProvider.getClub(eventInfo.clubId());
        Long eventId = eventService.create(eventInfo, club);

        return ResponseEntity.ok(new EventCreateResponse(eventId));
    }

    @GetMapping
    public ResponseEntity<PageResponse<EventOverviewGetResponse, Event>> getAll(@RequestParam EventCategory category,
                                                                                @PageableDefault(size = 1000) Pageable pageable) {

        Page<Event> events = eventService.getAll(category, pageable);

        List<EventOverviewGetResponse> responses = events.stream()
                .map(event -> EventOverviewGetResponse.from(event, eventImageService.getUrl()))
                .toList();

        return ResponseEntity.ok(new PageResponse<>(responses, events));
    }

    @PatchMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> update(
            @RequestPart(required = false) MultipartFile posterImage,
            @RequestPart EventUpdateRequest request,
            @RequestPart String category,
            @Authenticated JwtUser jwtUser
    ) {
        EventCategory eventCategory = EventCategory.valueOf(category);

        Event event = request.toEntity(eventCategory);

        eventService.update(event, jwtUser.id(), posterImage);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> delete(@PathVariable Long eventId, @Authenticated JwtUser jwtUser) {
        eventService.delete(eventId, jwtUser.id());

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/searches")
    public ResponseEntity<PageResponse<EventSearchOverviewGetResponse, Event>> search(
            @RequestParam String keyword,
            Pageable pageable
    ) {
        Page<Event> events = eventService.search(keyword, pageable);

        List<EventSearchOverviewGetResponse> responses = events.getContent().stream()
                .map(event -> EventSearchOverviewGetResponse.from(event, eventImageService.getUrl()))
                .toList();

        return ResponseEntity.ok(new PageResponse<>(responses, events));
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventDetailGetResponse> get(@PathVariable Long eventId, @Authenticated JwtUser jwtUser) {
        Long userId = jwtUser.id();

        Event event = eventService.get(eventId, userId);
        boolean hasAlreadyApplied = userEventService.hasAlreadyApplied(event.getId(), userId);

        EventGetInfo eventInfo = new EventGetInfo(event, hasAlreadyApplied);

        EventDetailGetResponse response = switch (event.getCategory()) {
            case SHOW -> EventDetailGetResponse.withShow(eventInfo, eventImageService.getUrl());
            case CLUB -> EventDetailGetResponse.withClub(eventInfo, eventImageService.getUrl());
            case PROMOTION -> EventDetailGetResponse.withPromotion(eventInfo, eventImageService.getUrl());
            case RECRUITMENT -> EventDetailGetResponse.withRecruitment(eventInfo, eventImageService.getUrl());
        };

        return ResponseEntity.ok(response);
    }

    @GetMapping("/banner")
    public ResponseEntity<List<EventBannerResponse>> getBanner() {
        LocalDateTime now = LocalDateTime.now();
        List<Event> events = eventService.getBanner(now, BANNER_LIMIT);

        List<EventBannerResponse> bannerResponses = events.stream()
                .map(event -> EventBannerResponse.from(event, eventImageService.getUrl()))
                .toList();

        return ResponseEntity.ok(bannerResponses);
    }

}
