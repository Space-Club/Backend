package com.spaceclub.invite.controller;

import com.spaceclub.global.Authenticated;
import com.spaceclub.global.jwt.vo.JwtUser;
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
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/clubs")
@RequiredArgsConstructor
public class InviteController {

    public static final String INVITE_LINK_PREFIX = "https://spaceclub.site/api/v1/clubs/invite/";

    private final InviteService inviteService;

    @PostMapping("/{clubId}/invite")
    public ResponseEntity<Void> createInviteCode(@PathVariable Long clubId,
                                                 @Authenticated JwtUser jwtUser,
                                                 UriComponentsBuilder uriBuilder) {

        String inviteCode = inviteService.createInviteCode(clubId, jwtUser.id());

        URI location = uriBuilder
                .path("/api/v1/clubs/{clubId}/invite/{inviteCode}")
                .buildAndExpand(clubId, inviteCode)
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @GetMapping("/{clubId}/invite")
    public ResponseEntity<InviteGetResponse> getInviteLink(@PathVariable Long clubId, @Authenticated JwtUser jwtUser) {
        Long userId = jwtUser.id();
        InviteGetInfo vo = inviteService.getInviteLink(clubId, userId);

        String inviteLink = INVITE_LINK_PREFIX + vo.inviteCode();
        boolean expired = vo.isExpired();

        return ResponseEntity.ok(new InviteGetResponse(inviteLink, expired));
    }

}
