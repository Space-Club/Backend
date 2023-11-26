package com.spaceclub.club.controller;

import com.spaceclub.club.controller.dto.ClubEventGetResponse;
import com.spaceclub.club.service.ClubEventService;
import com.spaceclub.event.service.vo.EventGetInfo;
import com.spaceclub.global.Authenticated;
import com.spaceclub.global.dto.PageResponse;
import com.spaceclub.global.jwt.vo.JwtUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/clubs")
@RequiredArgsConstructor
public class ClubEventController {

    private final ClubEventService clubEventService;

    @GetMapping("/{clubId}/events")
    public ResponseEntity<PageResponse<ClubEventGetResponse, EventGetInfo>> getClubEvents(@PathVariable Long clubId, Pageable pageable, @Authenticated JwtUser jwtUser) {
        Page<EventGetInfo> events = clubEventService.getClubEvents(clubId, pageable, jwtUser.id());

        List<ClubEventGetResponse> clubEventGetResponses = events.getContent()
                .stream()
                .map(ClubEventGetResponse::from)
                .toList();

        return ResponseEntity.ok(new PageResponse<>(clubEventGetResponses, events));
    }

}
