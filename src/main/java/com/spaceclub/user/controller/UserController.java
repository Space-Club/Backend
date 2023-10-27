package com.spaceclub.user.controller;

import com.spaceclub.event.domain.Event;
import com.spaceclub.user.controller.dto.EventPageResponse;
import com.spaceclub.user.controller.dto.EventResponse;
import com.spaceclub.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/{userId}/events")
    public EventPageResponse<EventResponse, Event> getAllEvents(@PathVariable Long userId, Pageable pageable) {
        Page<Event> eventPages = userService.findAllEventPages(userId, pageable);
        List<EventResponse> eventResponses = eventPages.getContent()
                .stream()
                .map(EventResponse::from)
                .toList();

        return new EventPageResponse<>(eventResponses, eventPages);
    }

}
