package com.spaceclub.invite.controller;

import com.spaceclub.global.jwt.service.JwtService;
import com.spaceclub.invite.service.InviteService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
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

    private final JwtService jwtService;

    @PostMapping("/clubs/{clubId}/invite")
    public ResponseEntity<Map<String, String>> getInviteLink(@PathVariable Long clubId) {
        String inviteCode = service.getInviteCode(clubId);

        String inviteLink = INVITE_LINK_PREFIX + inviteCode;

        return ResponseEntity.ok(
                Map.of("inviteLink", inviteLink)
        );
    }

    @PostMapping("/clubs/invite/{uuid}")
    public ResponseEntity<Void> joinClub(@PathVariable String uuid, HttpServletRequest httpServletRequest) {
        Long userId = jwtService.verifyUserId(httpServletRequest);

        service.joinClub(uuid, userId);

        return ResponseEntity.noContent().build();
    }

}
