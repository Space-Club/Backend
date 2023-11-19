package com.spaceclub.club.controller;

import com.spaceclub.club.controller.dto.ClubCreateRequest;
import com.spaceclub.club.controller.dto.ClubEventGetResponse;
import com.spaceclub.club.controller.dto.ClubGetResponse;
import com.spaceclub.club.controller.dto.ClubNoticeCreateRequest;
import com.spaceclub.club.controller.dto.ClubNoticeGetResponse;
import com.spaceclub.club.controller.dto.ClubNoticeUpdateRequest;
import com.spaceclub.club.controller.dto.ClubScheduleGetResponse;
import com.spaceclub.club.controller.dto.ClubScheduleGetResponse.ClubScheduleGetResponseInfo;
import com.spaceclub.club.controller.dto.ClubUpdateRequest;
import com.spaceclub.club.controller.dto.ClubUserUpdateRequest;
import com.spaceclub.club.controller.dto.MemberGetResponse;
import com.spaceclub.club.domain.Club;
import com.spaceclub.club.domain.ClubNotice;
import com.spaceclub.club.domain.ClubUser;
import com.spaceclub.club.service.ClubService;
import com.spaceclub.club.service.vo.ClubNoticeDelete;
import com.spaceclub.club.service.vo.ClubNoticeUpdate;
import com.spaceclub.club.service.vo.ClubUserUpdate;
import com.spaceclub.event.domain.Event;
import com.spaceclub.global.dto.PageResponse;
import com.spaceclub.global.jwt.service.JwtManager;
import com.spaceclub.invite.service.InviteService;
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

import static com.spaceclub.invite.controller.InviteController.INVITE_LINK_PREFIX;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ClubController {

    private final ClubService clubService;

    private final InviteService inviteService;

    private final JwtManager jwtManager;

    @PostMapping(value = "/clubs", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> createClub(@RequestPart(value = "request") ClubCreateRequest request,
                                             @RequestPart(value = "logoImage", required = false) MultipartFile logoImage,
                                             UriComponentsBuilder uriBuilder,
                                             HttpServletRequest httpServletRequest) throws IOException {

        Long userId = jwtManager.verifyUserId(httpServletRequest);
        Club newClub = request.toEntity();

        Club createdClub = clubService.createClub(newClub, userId, logoImage);
        Long id = createdClub.getId();

        URI location = uriBuilder
                .path("/api/v1/clubs/{id}")
                .buildAndExpand(id)
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @GetMapping("/clubs/{clubId}")
    public ResponseEntity<ClubGetResponse> getClub(@PathVariable Long clubId, HttpServletRequest httpServletRequest) {
        Club club = clubService.getClub(clubId);
        Long userId = jwtManager.verifyUserId(httpServletRequest);

        String inviteCode = inviteService.getInviteCode(clubId, userId);
        String role = clubService.getUserRole(clubId, userId);
        ClubGetResponse response = ClubGetResponse.from(club, INVITE_LINK_PREFIX + inviteCode, role);

        return ResponseEntity.ok(response);
    }

    @PatchMapping(value = "/clubs/{clubId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> updateClub(@PathVariable Long clubId,
                                           @RequestPart(value = "request", required = false) ClubUpdateRequest request,
                                           @RequestPart(value = "logoImage", required = false) MultipartFile logoImage,
                                           HttpServletRequest httpServletRequest) throws IOException {
        Long userId = jwtManager.verifyUserId(httpServletRequest);

        if (request == null) {
            request = new ClubUpdateRequest();
        }

        Club newClub = request.toEntity(clubId);
        clubService.updateClub(newClub, userId, logoImage);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/clubs/{clubId}")
    public ResponseEntity<String> deleteClub(@PathVariable Long clubId, HttpServletRequest httpServletRequest) {
        Long userId = jwtManager.verifyUserId(httpServletRequest);
        clubService.deleteClub(clubId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/clubs/{clubId}/events")
    public ResponseEntity<PageResponse<ClubEventGetResponse, Event>> getClubEvents(@PathVariable Long clubId, Pageable pageable) {
        Page<Event> events = clubService.getClubEvents(clubId, pageable);

        List<ClubEventGetResponse> clubEventGetResponses = events.getContent()
                .stream()
                .map(ClubEventGetResponse::from)
                .toList();

        return ResponseEntity.ok(new PageResponse<>(clubEventGetResponses, events));
    }

    @PatchMapping("/clubs/{clubId}/members/{memberId}")
    public ResponseEntity<Void> updateMemberRole(@PathVariable Long clubId, @PathVariable Long memberId, @RequestBody ClubUserUpdateRequest request) {
        clubService.updateMemberRole(new ClubUserUpdate(clubId, memberId, request.role()));

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/clubs/{clubId}/members")
    public ResponseEntity<List<MemberGetResponse>> getMembers(@PathVariable Long clubId) {
        List<ClubUser> clubUsers = clubService.getMembers(clubId);

        List<MemberGetResponse> response = clubUsers.stream()
                .map(MemberGetResponse::from)
                .toList();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/clubs/{clubId}/members/{memberId}")
    public ResponseEntity<Void> deleteMember(@PathVariable Long clubId, @PathVariable Long memberId) {
        clubService.deleteMember(clubId, memberId);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/clubs/{clubId}/notices")
    public ResponseEntity<Void> createNotice(@PathVariable Long clubId,
                                             @RequestBody ClubNoticeCreateRequest request,
                                             HttpServletRequest httpServletRequest) {
        Long userId = jwtManager.verifyUserId(httpServletRequest);

        clubService.createNotice(request.notice(), clubId, userId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/clubs/{clubId}/notices")
    public ResponseEntity<ClubNoticeGetResponse> getNotices(@PathVariable Long clubId, HttpServletRequest httpServletRequest) {
        Long userId = jwtManager.verifyUserId(httpServletRequest);

        List<ClubNotice> notices = clubService.getNotices(clubId, userId);

        ClubNoticeGetResponse response = new ClubNoticeGetResponse(notices);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/clubs/{clubId}/notices/{noticeId}")
    public ResponseEntity<Void> updateNotice(@PathVariable Long clubId,
                                             @PathVariable Long noticeId,
                                             @RequestBody ClubNoticeUpdateRequest request,
                                             HttpServletRequest httpServletRequest) {
        Long userId = jwtManager.verifyUserId(httpServletRequest);
        ClubNoticeUpdate vo = ClubNoticeUpdate.builder()
                .clubId(clubId)
                .noticeId(noticeId)
                .userId(userId)
                .notice(request.notice())
                .build();

        clubService.updateNotice(vo);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/clubs/{clubId}/notices/{noticeId}")
    public ResponseEntity<Void> deleteNotice(@PathVariable Long clubId,
                                             @PathVariable Long noticeId,
                                             HttpServletRequest httpServletRequest) {
        Long userId = jwtManager.verifyUserId(httpServletRequest);
        ClubNoticeDelete deleteVo = ClubNoticeDelete.builder()
                .clubId(clubId)
                .noticeId(noticeId)
                .userId(userId)
                .build();

        clubService.deleteNotice(deleteVo);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/clubs/{clubId}/schedules")
    public ResponseEntity<ClubScheduleGetResponse> getClubSchedule(@PathVariable Long clubId, HttpServletRequest httpServletRequest) {
        Long userId = jwtManager.verifyUserId(httpServletRequest);

        List<Event> events = clubService.getClubSchedules(clubId, userId);

        String profileImageUrl = clubService.getManagerProfileImageUrl(clubId);

        List<ClubScheduleGetResponseInfo> schedules = events.stream()
                .map((event -> ClubScheduleGetResponseInfo.builder()
                        .eventId(event.getId())
                        .title(event.getTitle())
                        .startDateTime(event.getFormOpenDateTime())
                        .endDateTime(event.getFormCloseDateTime())
                        .manager(event.getManagerName())
                        .profileImageUrl(profileImageUrl)
                        .build()))
                .toList();

        return ResponseEntity.ok(new ClubScheduleGetResponse(schedules));
    }

}
