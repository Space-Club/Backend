package com.spaceclub.user.controller;

import com.spaceclub.club.service.ClubProvider;
import com.spaceclub.club.service.vo.ClubInfo;
import com.spaceclub.event.service.UserEventProvider;
import com.spaceclub.event.service.vo.EventPageInfo;
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

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class ContentController {

    private final UserEventProvider userEventProvider;
    private final ClubProvider clubService;

    @GetMapping("/events")
    public PageResponse<UserEventGetResponse, EventPageInfo> getAllEvents(Pageable pageable, @Authenticated JwtUser jwtUser) {
        Page<EventPageInfo> eventPages = userEventProvider.findAllEventPages(jwtUser.id(), pageable);

        List<UserEventGetResponse> eventGetResponse = eventPages.getContent().stream()
                .map(UserEventGetResponse::from)
                .toList();

        return new PageResponse<>(eventGetResponse, eventPages);
    }

    @GetMapping("/clubs")
    public ResponseEntity<List<UserClubGetResponse>> getClubs(@Authenticated JwtUser jwtUser) {
        List<ClubInfo> clubs = clubService.getClubs(jwtUser.id());

        List<UserClubGetResponse> clubResponses = clubs.stream()
                .map(UserClubGetResponse::from)
                .collect(Collectors.toList());

        return ResponseEntity.ok(clubResponses);
    }

}
