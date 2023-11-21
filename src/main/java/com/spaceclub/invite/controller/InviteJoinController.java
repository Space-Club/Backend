package com.spaceclub.invite.controller;

import com.spaceclub.club.domain.Club;
import com.spaceclub.club.service.ClubMemberManagerService;
import com.spaceclub.global.Authenticated;
import com.spaceclub.global.jwt.vo.JwtUser;
import com.spaceclub.invite.controller.dto.ClubRequestToJoinResponse;
import com.spaceclub.invite.service.InviteJoinService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/clubs/invite")
@RequiredArgsConstructor
public class InviteJoinController {

    private final InviteJoinService inviteJoinService;

    private final ClubMemberManagerService clubMemberManagerService;

    @PostMapping("/{code}")
    public ResponseEntity<Map<String, Long>> joinClub(@PathVariable String code, @Authenticated JwtUser jwtUser) {

        Long clubId = inviteJoinService.joinClub(code, jwtUser.id());

        return ResponseEntity.ok(
                Map.of("clubId", clubId)
        );
    }

    @GetMapping("/{code}")
    public ResponseEntity<ClubRequestToJoinResponse> requestToJoinClub(@PathVariable String code) {
        Club club = inviteJoinService.requestToJoinClub(code);

        Long memberCount = clubMemberManagerService.countMember(club);

        ClubRequestToJoinResponse response = ClubRequestToJoinResponse.from(club, memberCount);

        return ResponseEntity.ok(response);
    }

}
