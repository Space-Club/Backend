package com.spaceclub.event.controller;

import com.spaceclub.event.controller.dto.EventParticipationCreateRequest;
import com.spaceclub.event.controller.dto.EventParticipationDeleteResponse;
import com.spaceclub.event.domain.ParticipationStatus;
import com.spaceclub.event.service.ParticipationService;
import com.spaceclub.event.service.vo.EventParticipationCreateInfo;
import com.spaceclub.global.Authenticated;
import com.spaceclub.global.jwt.vo.JwtUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class ParticipationController {

    private final ParticipationService participationService;

    @PostMapping(value = {"/applications", "/participate"})
    public ResponseEntity<Void> apply(@RequestBody EventParticipationCreateRequest request, @Authenticated JwtUser jwtUser) {
        EventParticipationCreateInfo eventParticipationCreateInfo = EventParticipationCreateInfo.builder()
                .userId(jwtUser.id())
                .eventId(request.eventId())
                .formOptionUsers(request.toEntityList())
                .ticketCount(request.ticketCount())
                .build();

        participationService.apply(eventParticipationCreateInfo);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(value = {"/{eventId}/applications", "/{eventId}/participate"})
    public ResponseEntity<EventParticipationDeleteResponse> cancel(@PathVariable Long eventId, @Authenticated JwtUser jwtUser) {
        ParticipationStatus status = participationService.cancel(eventId, jwtUser.id());

        return ResponseEntity.ok(new EventParticipationDeleteResponse(status));
    }

}
