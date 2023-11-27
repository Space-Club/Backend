package com.spaceclub.club.controller;

import com.spaceclub.club.controller.dto.ClubEventOverviewGetResponse;
import com.spaceclub.club.service.ClubEventService;
import com.spaceclub.event.service.vo.ClubEventOverviewGetInfo;
import com.spaceclub.global.Authenticated;
import com.spaceclub.global.dto.PageResponse;
import com.spaceclub.global.jwt.vo.JwtUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
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
    public ResponseEntity<PageResponse<ClubEventOverviewGetResponse, ClubEventOverviewGetInfo>> getClubEvents(@PathVariable Long clubId, @PageableDefault(size = 1000) Pageable pageable, @Authenticated JwtUser jwtUser) {
        Page<ClubEventOverviewGetInfo> events = clubEventService.getClubEvents(clubId, pageable, jwtUser.id());

        List<ClubEventOverviewGetResponse> clubEventOverviewGetResponse = events.getContent()
                .stream()
                .map(ClubEventOverviewGetResponse::from)
                .toList();

        return ResponseEntity.ok(new PageResponse<>(clubEventOverviewGetResponse, events));
    }

}
