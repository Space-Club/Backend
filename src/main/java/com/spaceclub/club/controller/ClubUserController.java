package com.spaceclub.club.controller;

import com.spaceclub.club.controller.dto.ClubUserRoleResponse;
import com.spaceclub.club.controller.dto.ClubUserUpdateRequest;
import com.spaceclub.club.controller.dto.MemberGetResponse;
import com.spaceclub.club.service.ClubMemberManagerService;
import com.spaceclub.club.service.vo.ClubUserUpdate;
import com.spaceclub.global.Authenticated;
import com.spaceclub.global.jwt.vo.JwtUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/clubs")
@RequiredArgsConstructor
public class ClubUserController {

    private final ClubMemberManagerService clubMemberManagerService;

    @GetMapping("/{clubId}/users")
    public ResponseEntity<ClubUserRoleResponse> getClubUserRole(@PathVariable Long clubId, @Authenticated JwtUser jwtUser) {
        String role = clubMemberManagerService.getUserRole(clubId, jwtUser.id());

        return ResponseEntity.ok(new ClubUserRoleResponse(role));
    }

    @PatchMapping("/{clubId}/members/{memberId}")
    public ResponseEntity<Void> updateMemberRole(@PathVariable Long clubId, @PathVariable Long memberId, @RequestBody ClubUserUpdateRequest request, @Authenticated JwtUser jwtUser) {
        clubMemberManagerService.updateMemberRole(new ClubUserUpdate(clubId, memberId, request.role(), jwtUser.id()));

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{clubId}/members")
    public ResponseEntity<List<MemberGetResponse>> getMembers(@PathVariable Long clubId, @Authenticated JwtUser jwtUser) {
        List<MemberGetResponse> response = clubMemberManagerService.getMembers(clubId, jwtUser.id());

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{clubId}/members/{memberId}")
    public ResponseEntity<Void> deleteMember(@PathVariable Long clubId, @PathVariable Long memberId, @Authenticated JwtUser jwtUser) {
        clubMemberManagerService.deleteMember(clubId, memberId, jwtUser.id());

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{clubId}/users")
    public ResponseEntity<Void> exitClub(@PathVariable Long clubId, @Authenticated JwtUser jwtUser) {
        clubMemberManagerService.exitClub(clubId, jwtUser.id());

        return ResponseEntity.noContent().build();
    }

}
