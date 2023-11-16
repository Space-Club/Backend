package com.spaceclub.event.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.spaceclub.event.controller.dto.EventApplicationDeleteResponse;
import com.spaceclub.event.controller.dto.EventCreateResponse;
import com.spaceclub.event.controller.dto.EventDetailGetResponse;
import com.spaceclub.event.controller.dto.EventGetResponse;
import com.spaceclub.event.controller.dto.EventSearchGetResponse;
import com.spaceclub.event.controller.dto.createRequest.ClubEventCreateRequest;
import com.spaceclub.event.controller.dto.createRequest.PromotionEventCreateRequest;
import com.spaceclub.event.controller.dto.createRequest.RecruitmentEventCreateRequest;
import com.spaceclub.event.controller.dto.createRequest.ShowEventCreateRequest;
import com.spaceclub.event.controller.dto.updateRequest.ClubEventUpdateRequest;
import com.spaceclub.event.controller.dto.updateRequest.PromotionEventUpdateRequest;
import com.spaceclub.event.controller.dto.updateRequest.RecruitmentEventUpdateRequest;
import com.spaceclub.event.controller.dto.updateRequest.ShowEventUpdateRequest;
import com.spaceclub.event.domain.ApplicationStatus;
import com.spaceclub.event.domain.Event;
import com.spaceclub.event.domain.EventCategory;
import com.spaceclub.event.service.EventService;
import com.spaceclub.event.controller.dto.EventApplicationCreateRequest;
import com.spaceclub.event.service.vo.EventApplicationCreateInfo;
import com.spaceclub.global.S3ImageUploader;
import com.spaceclub.global.dto.PageResponse;
import com.spaceclub.global.jwt.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    private final S3ImageUploader uploader;

    private final JwtService jwtService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<EventCreateResponse> create(
            @RequestPart MultipartFile posterImage,
            @RequestPart String request,
            @RequestPart String category,
            HttpServletRequest servletRequest
    ) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        String posterImageUrl = uploader.uploadPosterImage(posterImage);
        EventCategory eventCategory = EventCategory.valueOf(category);

        Long clubId;
        Event event;

        switch (EventCategory.valueOf(category)) {
            case SHOW -> {
                ShowEventCreateRequest showEventCreateRequest = objectMapper.readValue(request, ShowEventCreateRequest.class);
                event = showEventCreateRequest.toEntity(eventCategory, posterImageUrl);
                clubId = showEventCreateRequest.clubId();
            }
            case PROMOTION -> {
                PromotionEventCreateRequest promotionEventCreateRequest = objectMapper.readValue(request, PromotionEventCreateRequest.class);
                event = promotionEventCreateRequest.toEntity(eventCategory, posterImageUrl);
                clubId = promotionEventCreateRequest.clubId();
            }
            case RECRUITMENT -> {
                RecruitmentEventCreateRequest recruitmentEventCreateRequest = objectMapper.readValue(request, RecruitmentEventCreateRequest.class);
                event = recruitmentEventCreateRequest.toEntity(eventCategory, posterImageUrl);
                clubId = recruitmentEventCreateRequest.clubId();
            }
            case CLUB -> {
                ClubEventCreateRequest clubEventCreateRequest = objectMapper.readValue(request, ClubEventCreateRequest.class);
                event = clubEventCreateRequest.toEntity(eventCategory, posterImageUrl);
                clubId = clubEventCreateRequest.clubId();
            }
            default -> throw new IllegalArgumentException("존재하지 않는 행사의 카테고리입니다");
        }

        Long userId = jwtService.verifyUserId(servletRequest);
        Long eventId = eventService.create(event, clubId, userId);

        return ResponseEntity.ok(new EventCreateResponse(eventId));
    }


    @GetMapping
    public ResponseEntity<PageResponse<EventGetResponse, Event>> getEvents(Pageable pageable) {
        Page<Event> events = eventService.getAll(pageable);

        List<EventGetResponse> eventGetResponses = events.getContent().stream().map(EventGetResponse::from).toList();

        return ResponseEntity.ok(new PageResponse<>(eventGetResponses, events));
    }

    @PatchMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> updateEvent(
            @RequestPart MultipartFile posterImage,
            @RequestPart String request,
            @RequestPart String category,
            HttpServletRequest servletRequest
    ) throws IOException {

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        String posterImageUrl = uploader.uploadPosterImage(posterImage);
        EventCategory eventCategory = EventCategory.valueOf(category);

        Event event;

        switch (category) {
            case "SHOW" -> {
                ShowEventUpdateRequest showEventUpdateRequest = objectMapper.readValue(request, ShowEventUpdateRequest.class);
                event = showEventUpdateRequest.toEntity(eventCategory, posterImageUrl);
            }
            case "PROMOTION" -> {
                PromotionEventUpdateRequest promotionEventUpdateRequest = objectMapper.readValue(request, PromotionEventUpdateRequest.class);
                event = promotionEventUpdateRequest.toEntity(eventCategory, posterImageUrl);
            }
            case "RECRUITMENT" -> {
                RecruitmentEventUpdateRequest recruitmentEventUpdateRequest = objectMapper.readValue(request, RecruitmentEventUpdateRequest.class);
                event = recruitmentEventUpdateRequest.toEntity(eventCategory, posterImageUrl);
            }
            case "CLUB" -> {
                ClubEventUpdateRequest clubEventUpdateRequest = objectMapper.readValue(request, ClubEventUpdateRequest.class);
                event = clubEventUpdateRequest.toEntity(eventCategory, posterImageUrl);
            }
            default -> throw new IllegalArgumentException("존재하지 않는 행사의 카테고리입니다");
        }

        Long userId = jwtService.verifyUserId(servletRequest);
        eventService.update(event, userId);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{eventId}")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long eventId, HttpServletRequest servletRequest) {
        Long userId = jwtService.verifyUserId(servletRequest);
        eventService.delete(eventId, userId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/searches")
    public ResponseEntity<PageResponse<EventSearchGetResponse, Event>> getSearchEvents(@RequestParam String keyword, Pageable pageable, HttpServletRequest servletRequest) {
        Long userId = jwtService.verifyUserId(servletRequest);
        Page<Event> events = eventService.getSearchEvents(keyword, pageable, userId);

        List<EventSearchGetResponse> eventSearchGetResponses = events.getContent().stream().map(EventSearchGetResponse::from).toList();

        return ResponseEntity.ok(new PageResponse<>(eventSearchGetResponses, events));
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventDetailGetResponse> getEventDetail(@PathVariable Long eventId) {
        Event event = eventService.get(eventId);
        EventDetailGetResponse response = EventDetailGetResponse.from(event);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/applications")
    public ResponseEntity<Void> createApplicationForm(@RequestBody EventApplicationCreateRequest request, HttpServletRequest servletRequest) {
        Long userId = jwtService.verifyUserId(servletRequest);

        EventApplicationCreateInfo eventApplicationCreateInfo = EventApplicationCreateInfo.builder()
                .userId(userId)
                .eventId(request.eventId())
                .formOptionUsers(request.toEntityList())
                .ticketCount(request.ticketCount())
                .build();
        eventService.createApplicationForm(eventApplicationCreateInfo);

        return ResponseEntity.noContent().build();
    }


    @DeleteMapping("/{eventId}/applications")
    public ResponseEntity<EventApplicationDeleteResponse> cancelEvent(@PathVariable Long eventId, HttpServletRequest servletRequest) {
        Long userId = jwtService.verifyUserId(servletRequest);

        ApplicationStatus status = eventService.cancelEvent(eventId, userId);

        return ResponseEntity.ok(new EventApplicationDeleteResponse(status));
    }

}
