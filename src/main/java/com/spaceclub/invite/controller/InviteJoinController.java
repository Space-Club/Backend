package com.spaceclub.invite.controller;

import com.spaceclub.club.domain.Club;
import com.spaceclub.club.service.ClubMemberManagerService;
import com.spaceclub.global.Authenticated;
import com.spaceclub.global.config.s3.properties.S3Properties;
import com.spaceclub.global.jwt.vo.JwtUser;
import com.spaceclub.invite.controller.dto.ClubRequestToJoinResponse;
import com.spaceclub.invite.controller.dto.JoinClubResponse;
import com.spaceclub.invite.service.InviteJoinService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/clubs/invites")
@RequiredArgsConstructor
public class InviteJoinController {

    private final InviteJoinService inviteJoinService;

    private final ClubMemberManagerService clubMemberManagerService;

    private final S3Properties s3Properties;

    @PostMapping("/{code}")
    public ResponseEntity<JoinClubResponse> joinClub(@PathVariable String code, @Authenticated JwtUser jwtUser) {

        Long clubId = inviteJoinService.joinClub(code, jwtUser.id());

        JoinClubResponse response = new JoinClubResponse(clubId);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{code}")
    public ResponseEntity<ClubRequestToJoinResponse> requestToJoinClub(@PathVariable String code) {
        Club club = inviteJoinService.requestToJoinClub(code);

        Long memberCount = clubMemberManagerService.countMember(club);

        ClubRequestToJoinResponse response = ClubRequestToJoinResponse.from(club, memberCount, s3Properties.url());

        return ResponseEntity.ok(response);
    }

}
