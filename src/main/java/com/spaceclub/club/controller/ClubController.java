package com.spaceclub.club.controller;

import com.spaceclub.club.controller.dto.ClubCreateRequest;
import com.spaceclub.club.controller.dto.ClubEventGetResponse;
import com.spaceclub.club.controller.dto.ClubGetResponse;
import com.spaceclub.club.controller.dto.ClubUserUpdateRequest;
import com.spaceclub.club.controller.dto.MemberGetResponse;
import com.spaceclub.club.domain.Club;
import com.spaceclub.club.domain.ClubUser;
import com.spaceclub.club.service.ClubService;
import com.spaceclub.club.service.vo.ClubUserUpdate;
import com.spaceclub.event.domain.Event;
import com.spaceclub.global.S3ImageUploader;
import com.spaceclub.global.dto.PageResponse;
import com.spaceclub.global.jwt.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ClubController {

    private final ClubService service;

    private final S3ImageUploader uploader;

    private final JwtService jwtService;

    private static final String INVITE_FIXED_URL = "https://spaceclub.site/api/v1/clubs/invite/";

    @PostMapping(value = "/clubs", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> createClub(@RequestPart(value = "request") ClubCreateRequest request,
                                             @RequestPart(value = "logoImage", required = false) MultipartFile logoImage,
                                             UriComponentsBuilder uriBuilder,
                                             HttpServletRequest httpServletRequest) throws IOException {

        Long userId = jwtService.verifyUserId(httpServletRequest);

        String logoImageUrl = null;
        if (logoImage != null) {
            logoImageUrl = uploader.uploadClubLogoImage(logoImage);
        }

        Club newClub = request.toEntity(logoImageUrl);
        Club createdClub = service.createClub(newClub, userId);
        Long id = createdClub.getId();

        URI location = uriBuilder
                .path("/api/v1/clubs/{id}")
                .buildAndExpand(id)
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @GetMapping("/clubs/{clubId}")
    public ResponseEntity<ClubGetResponse> getClub(@PathVariable Long clubId) {
        Club club = service.getClub(clubId);
        ClubGetResponse response = ClubGetResponse.from(club);

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/clubs/{clubId}")
    public ResponseEntity<String> deleteClub(@PathVariable Long clubId) {
        service.deleteClub(clubId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/clubs/{clubId}/events")
    public ResponseEntity<PageResponse<ClubEventGetResponse, Event>> getClubEvents(@PathVariable Long clubId, Pageable pageable) {
        Page<Event> events = service.getClubEvents(clubId, pageable);

        List<ClubEventGetResponse> clubEventGetResponses = events.getContent()
                .stream()
                .map(ClubEventGetResponse::from)
                .toList();

        return ResponseEntity.ok(new PageResponse<>(clubEventGetResponses, events));
    }

    @PatchMapping("/clubs/{clubId}/members/{memberId}")
    public ResponseEntity<Void> updateMemberRole(@PathVariable Long clubId, @PathVariable Long memberId, @RequestBody ClubUserUpdateRequest request) {
        service.updateMemberRole(new ClubUserUpdate(clubId, memberId, request.role()));

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/clubs/{clubId}/members")
    public ResponseEntity<List<MemberGetResponse>> getMembers(@PathVariable Long clubId) {
        List<ClubUser> clubUsers = service.getMembers(clubId);

        List<MemberGetResponse> response = clubUsers.stream()
                .map(MemberGetResponse::from)
                .toList();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/clubs/{clubId}/members/{memberId}")
    public ResponseEntity<Void> deleteMember(@PathVariable Long clubId, @PathVariable Long memberId) {
        service.deleteMember(clubId, memberId);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/clubs/{clubId}/invite")
    public ResponseEntity<Map<String, String>> getInvitationCode(@PathVariable Long clubId) {
        String uuid = service.getInvitationCode(clubId);

        String invitationCode = INVITE_FIXED_URL + uuid;

        return ResponseEntity.ok(
                Map.of("invitationCode", invitationCode)
        );
    }

    @PostMapping("/clubs/invite/{uuid}")
    public ResponseEntity<Void> joinClub(@PathVariable String uuid) {
        boolean isSuccess = service.joinClub(uuid);
        if (isSuccess) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.badRequest().build();
    }

}
