package com.spaceclub.event.controller;

import com.spaceclub.event.controller.dto.CreateEventRequest;
import com.spaceclub.event.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.net.URI;

@Controller
@RequestMapping("/api/v1/event")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    @PostMapping
    public ResponseEntity<Void> saveEvent(@RequestBody CreateEventRequest request) {
        Long eventId = eventService.create(request.toEntity());

        return ResponseEntity.created(URI.create("https/" + eventId)).build();
    }

}
