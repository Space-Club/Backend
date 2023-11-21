package com.spaceclub.invite.controller;

import com.spaceclub.global.Authenticated;
import com.spaceclub.global.jwt.vo.JwtUser;
import com.spaceclub.invite.service.InviteService;
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

    private final InviteService inviteService;

    @PostMapping("/clubs/{clubId}/invite")
    public ResponseEntity<Map<String, String>> getInviteLink(@PathVariable Long clubId, @Authenticated JwtUser jwtUser) {

        String inviteCode = inviteService.getInviteCode(clubId, jwtUser.id());

        String inviteLink = INVITE_LINK_PREFIX + inviteCode;

        return ResponseEntity.ok(
                Map.of("inviteLink", inviteLink)
        );
    }

}
