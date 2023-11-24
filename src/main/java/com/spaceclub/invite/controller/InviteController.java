package com.spaceclub.invite.controller;

import com.spaceclub.global.Authenticated;
import com.spaceclub.global.jwt.vo.JwtUser;
import com.spaceclub.invite.controller.dto.InviteCreateAndGetResponse;
import com.spaceclub.invite.controller.dto.InviteGetResponse;
import com.spaceclub.invite.service.InviteService;
import com.spaceclub.invite.service.vo.InviteGetInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/clubs")
@RequiredArgsConstructor
public class InviteController {

    public static final String INVITE_LINK_PREFIX = "https://spaceclub.site/api/v1/clubs/invite/";

    private final InviteService inviteService;

    @PostMapping("/{clubId}/invite")
    public ResponseEntity<InviteCreateAndGetResponse> createAndGetInviteLink(@PathVariable Long clubId, @Authenticated JwtUser jwtUser) {

        String inviteCode = inviteService.createAndGetInviteLink(clubId, jwtUser.id());

        String inviteLink = INVITE_LINK_PREFIX + inviteCode;

        return ResponseEntity.ok(new InviteCreateAndGetResponse(inviteLink));
    }

    @GetMapping("/{clubId}/invite")
    public ResponseEntity<InviteGetResponse> getInviteLink(@PathVariable Long clubId, @Authenticated JwtUser jwtUser) {
        Long userId = jwtUser.id();
        InviteGetInfo vo = inviteService.getInviteLink(clubId, userId);

        String inviteLink = INVITE_LINK_PREFIX + vo.inviteCode();
        Boolean expired = vo.isExpired();

        return ResponseEntity.ok(new InviteGetResponse(inviteLink, expired));
    }

}
