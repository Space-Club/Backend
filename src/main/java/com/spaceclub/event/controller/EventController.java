package com.spaceclub.event.controller;

import com.spaceclub.club.controller.EventCreateConverter;
import com.spaceclub.club.controller.EventUpdateConverter;
import com.spaceclub.event.controller.dto.EventCreateResponse;
import com.spaceclub.event.controller.dto.EventDetailGetResponse;
import com.spaceclub.event.controller.dto.EventOverviewGetResponse;
import com.spaceclub.event.domain.Event;
import com.spaceclub.event.domain.EventCategory;
import com.spaceclub.event.service.EventService;
import com.spaceclub.event.service.vo.EventCreateInfo;
import com.spaceclub.global.Authenticated;
import com.spaceclub.global.dto.PageResponse;
import com.spaceclub.global.jwt.vo.JwtUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

import java.util.List;

@Controller
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<EventCreateResponse> create(
            @RequestPart MultipartFile posterImage,
            @RequestPart String request,
            @RequestPart String category,
            @Authenticated JwtUser jwtUser
    ) {

        EventCategory eventCategory = EventCategory.valueOf(category);
        EventCreateConverter eventCreateConverter = EventCreateConverter.of(request, eventCategory);

        EventCreateInfo createInfo = EventCreateInfo.builder()
                .event(eventCreateConverter.event())
                .clubId(eventCreateConverter.clubId())
                .userId(jwtUser.id())
                .posterImage(posterImage)
                .build();

        Long eventId = eventService.create(createInfo);

        return ResponseEntity.ok(new EventCreateResponse(eventId));
    }

    @GetMapping
    public ResponseEntity<PageResponse<EventOverviewGetResponse, Event>> getAll(@RequestParam EventCategory category, Pageable pageable) {
        Page<Event> events = eventService.getAll(category, pageable);

        List<EventOverviewGetResponse> responses = events.getContent()
                .stream()
                .map(EventOverviewGetResponse::from)
                .toList();

        return ResponseEntity.ok(new PageResponse<>(responses, events));
    }

    @PatchMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> update(
            @RequestPart MultipartFile posterImage,
            @RequestPart String request,
            @RequestPart String category,
            @Authenticated JwtUser jwtUser
    ) {
        EventCategory eventCategory = EventCategory.valueOf(category);

        EventUpdateConverter eventUpdateConverter = EventUpdateConverter.of(request, eventCategory);

        eventService.update(eventUpdateConverter.event(), jwtUser.id(), posterImage);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> delete(@PathVariable Long eventId, @Authenticated JwtUser jwtUser) {
        eventService.delete(eventId, jwtUser.id());

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/searches")
    public ResponseEntity<PageResponse<EventOverviewGetResponse, Event>> search(
            @RequestParam String keyword,
            Pageable pageable
    ) {
        Page<Event> events = eventService.search(keyword, pageable);

        List<EventOverviewGetResponse> responses = events.getContent().stream().map(EventOverviewGetResponse::from).toList();

        return ResponseEntity.ok(new PageResponse<>(responses, events));
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventDetailGetResponse> get(@PathVariable Long eventId, @Authenticated JwtUser jwtUser) {
        Long userId = jwtUser.id();

        Event event = eventService.get(eventId, userId);

        EventCategory category = event.getCategory();

        EventDetailGetResponse response = switch (category) {
            case SHOW -> EventDetailGetResponse.withShow(event, true, true, true);
            case CLUB -> EventDetailGetResponse.withClub(event, true, true, true);
            case PROMOTION -> EventDetailGetResponse.withPromotion(event, true, true, true);
            case RECRUITMENT -> EventDetailGetResponse.withRecruitment(event, true, true, true);
        };

        return ResponseEntity.ok(response);
    }

}
