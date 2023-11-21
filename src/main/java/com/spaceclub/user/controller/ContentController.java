package com.spaceclub.user.controller;

import com.spaceclub.club.domain.Club;
import com.spaceclub.club.service.ClubService;
import com.spaceclub.event.domain.Event;
import com.spaceclub.event.service.EventService;
import com.spaceclub.event.service.ParticipationService;
import com.spaceclub.global.Authenticated;
import com.spaceclub.global.dto.PageResponse;
import com.spaceclub.global.jwt.vo.JwtUser;
import com.spaceclub.user.controller.dto.UserClubGetResponse;
import com.spaceclub.user.controller.dto.UserEventGetResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

import static com.spaceclub.user.controller.dto.UserEventGetResponse.from;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class ContentController {

    private final ParticipationService participationService;
    private final ClubService clubService;

    @GetMapping("/events")
    public PageResponse<UserEventGetResponse, Event> getAllEvents(Pageable pageable, @Authenticated JwtUser jwtUser) {
        Page<Event> eventPages = participationService.findAllEventPages(jwtUser.id(), pageable);

        List<UserEventGetResponse> eventGetResponse = eventPages.getContent().stream()
                .map(event -> from(event, participationService.findEventStatus(jwtUser.id(), event)))
                .toList();

        return new PageResponse<>(eventGetResponse, eventPages);
    }

    @GetMapping("/clubs")
    public ResponseEntity<List<UserClubGetResponse>> getClubs(@Authenticated JwtUser jwtUser) {
        List<Club> clubs = clubService.getClubs(jwtUser.id());

        List<UserClubGetResponse> clubResponses = clubs.stream()
                .map(UserClubGetResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(clubResponses);
    }

}
