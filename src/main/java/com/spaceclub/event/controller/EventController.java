package com.spaceclub.event.controller;

import com.spaceclub.event.controller.dto.EventApplyRequest;
import com.spaceclub.event.controller.dto.EventCreateRequest;
import com.spaceclub.event.controller.dto.EventGetResponse;
import com.spaceclub.event.domain.Event;
import com.spaceclub.event.service.EventService;
import com.spaceclub.global.S3ImageUploader;
import com.spaceclub.global.dto.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.util.List;

@Controller
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    private final S3ImageUploader uploader;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> create(@RequestPart EventCreateRequest request, @RequestPart MultipartFile poster) throws IOException {
        String fileName = uploader.uploadImage(poster);
        Long eventId = eventService.create(request.toEntity(fileName), request.clubId());

        return ResponseEntity.created(URI.create("/api/v1/events/" + eventId)).build();
    }

    @GetMapping
    public ResponseEntity<PageResponse<EventGetResponse, Event>> getEvents(Pageable pageable) {
        Page<Event> events = eventService.getAll(pageable);

        List<EventGetResponse> eventGetResponses = events.getContent()
                .stream()
                .map(EventGetResponse::from)
                .toList();

        return ResponseEntity.ok(new PageResponse<>(eventGetResponses, events));
    }

    @PostMapping("/apply")
    public ResponseEntity<Void> applyEvent(@RequestBody EventApplyRequest request) {
        Long eventId = request.eventId();
        Long userId = request.userId();
        eventService.applyEvent(eventId, userId);

        return ResponseEntity.ok().build();
    }

}
