package com.spaceclub.invite.controller;

import com.spaceclub.club.domain.Club;
import com.spaceclub.club.service.ClubService;
import com.spaceclub.global.jwt.service.JwtService;
import com.spaceclub.invite.controller.dto.ClubRequestToJoinResponse;
import com.spaceclub.invite.service.InviteService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class InviteController {

    public static final String INVITE_LINK_PREFIX = "https://spaceclub.site/api/v1/clubs/invite/";

    private final InviteService service;

    private final ClubService clubService;

    private final JwtService jwtService;

    @PostMapping("/clubs/{clubId}/invite")
    public ResponseEntity<Map<String, String>> getInviteLink(@PathVariable Long clubId, HttpServletRequest httpServletRequest) {
        Long userId = jwtService.verifyUserId(httpServletRequest);

        String inviteCode = service.getInviteCode(clubId, userId);

        String inviteLink = INVITE_LINK_PREFIX + inviteCode;

        return ResponseEntity.ok(
                Map.of("inviteLink", inviteLink)
        );
    }

    @PostMapping("/clubs/invite/{code}")
    public ResponseEntity<Map<String, Long>> joinClub(@PathVariable String code, HttpServletRequest httpServletRequest) {
        Long userId = jwtService.verifyUserId(httpServletRequest);

        Long clubId = service.joinClub(code, userId);

        return ResponseEntity.ok(
                Map.of("clubId", clubId)
        );
    }

    @GetMapping("/clubs/invite/{code}")
    public ResponseEntity<ClubRequestToJoinResponse> requestToJoinClub(@PathVariable String code) {
        Club club = service.requestToJoinClub(code);

        Long memberCount = clubService.countMember(club);

        ClubRequestToJoinResponse response = ClubRequestToJoinResponse.from(club, memberCount);

        return ResponseEntity.ok(response);
    }

}
