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
import com.spaceclub.club.controller.dto.ClubUserRoleResponse;
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
import com.spaceclub.global.Authenticated;
import com.spaceclub.global.dto.PageResponse;
import com.spaceclub.global.jwt.vo.JwtUser;
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

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ClubController {

    private final ClubService clubService;

    @PostMapping(value = "/clubs", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> createClub(@RequestPart(value = "request") ClubCreateRequest request,
                                             @RequestPart(value = "logoImage", required = false) MultipartFile logoImage,
                                             UriComponentsBuilder uriBuilder,
                                             @Authenticated JwtUser jwtUser) {
        Club newClub = request.toEntity();

        Club createdClub = clubService.createClub(newClub, jwtUser.id(), logoImage);
        Long id = createdClub.getId();

        URI location = uriBuilder
                .path("/api/v1/clubs/{id}")
                .buildAndExpand(id)
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @GetMapping("/clubs/{clubId}")
    public ResponseEntity<ClubGetResponse> getClub(@PathVariable Long clubId, @Authenticated JwtUser jwtUser) {
        Club club = clubService.getClub(clubId, jwtUser.id());

        ClubGetResponse response = ClubGetResponse.from(club);

        return ResponseEntity.ok(response);
    }

    @GetMapping("/clubs/{clubId}/users")
    public ResponseEntity<ClubUserRoleResponse> getClubUserRole(@PathVariable Long clubId, @Authenticated JwtUser jwtUser) {
        String role = clubService.getUserRole(clubId, jwtUser.id());

        return ResponseEntity.ok(new ClubUserRoleResponse(role));
    }

    @PatchMapping(value = "/clubs/{clubId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> updateClub(@PathVariable Long clubId,
                                           @RequestPart(value = "request", required = false) ClubUpdateRequest request,
                                           @RequestPart(value = "logoImage", required = false) MultipartFile logoImage,
                                           @Authenticated JwtUser jwtUser) {

        if (request == null) {
            request = new ClubUpdateRequest();
        }

        Club newClub = request.toEntity(clubId);
        clubService.updateClub(newClub, jwtUser.id(), logoImage);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/clubs/{clubId}")
    public ResponseEntity<String> deleteClub(@PathVariable Long clubId, @Authenticated JwtUser jwtUser) {
        clubService.deleteClub(clubId, jwtUser.id());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/clubs/{clubId}/events")
    public ResponseEntity<PageResponse<ClubEventGetResponse, Event>> getClubEvents(@PathVariable Long clubId, Pageable pageable, @Authenticated JwtUser jwtUser) {
        Page<Event> events = clubService.getClubEvents(clubId, pageable, jwtUser.id());

        List<ClubEventGetResponse> clubEventGetResponses = events.getContent()
                .stream()
                .map(ClubEventGetResponse::from)
                .toList();

        return ResponseEntity.ok(new PageResponse<>(clubEventGetResponses, events));
    }

    @PatchMapping("/clubs/{clubId}/members/{memberId}")
    public ResponseEntity<Void> updateMemberRole(@PathVariable Long clubId, @PathVariable Long memberId, @RequestBody ClubUserUpdateRequest request, @Authenticated JwtUser jwtUser) {
        clubService.updateMemberRole(new ClubUserUpdate(clubId, memberId, request.role(), jwtUser.id()));

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/clubs/{clubId}/members")
    public ResponseEntity<List<MemberGetResponse>> getMembers(@PathVariable Long clubId, @Authenticated JwtUser jwtUser) {
        List<ClubUser> clubUsers = clubService.getMembers(clubId, jwtUser.id());

        List<MemberGetResponse> response = clubUsers.stream()
                .sorted(ClubUser.memberComparator)
                .map(MemberGetResponse::from)
                .sorted(MemberGetResponse.memberComparator)
                .toList();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/clubs/{clubId}/members/{memberId}")
    public ResponseEntity<Void> deleteMember(@PathVariable Long clubId, @PathVariable Long memberId, @Authenticated JwtUser jwtUser) {
        clubService.deleteMember(clubId, memberId, jwtUser.id());

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/clubs/{clubId}/notices")
    public ResponseEntity<Void> createNotice(@PathVariable Long clubId,
                                             @RequestBody ClubNoticeCreateRequest request,
                                             @Authenticated JwtUser jwtUser) {
        clubService.createNotice(request.notice(), clubId, jwtUser.id());

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/clubs/{clubId}/notices")
    public ResponseEntity<ClubNoticeGetResponse> getNotices(@PathVariable Long clubId, @Authenticated JwtUser jwtUser) {
        List<ClubNotice> notices = clubService.getNotices(clubId, jwtUser.id());

        ClubNoticeGetResponse response = new ClubNoticeGetResponse(notices);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/clubs/{clubId}/notices/{noticeId}")
    public ResponseEntity<Void> updateNotice(@PathVariable Long clubId,
                                             @PathVariable Long noticeId,
                                             @RequestBody ClubNoticeUpdateRequest request,
                                             @Authenticated JwtUser jwtUser) {
        ClubNoticeUpdate vo = ClubNoticeUpdate.builder()
                .clubId(clubId)
                .noticeId(noticeId)
                .userId(jwtUser.id())
                .notice(request.notice())
                .build();

        clubService.updateNotice(vo);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/clubs/{clubId}/notices/{noticeId}")
    public ResponseEntity<Void> deleteNotice(@PathVariable Long clubId,
                                             @PathVariable Long noticeId,
                                             @Authenticated JwtUser jwtUser) {
        ClubNoticeDelete deleteVo = ClubNoticeDelete.builder()
                .clubId(clubId)
                .noticeId(noticeId)
                .userId(jwtUser.id())
                .build();

        clubService.deleteNotice(deleteVo);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/clubs/{clubId}/schedules")
    public ResponseEntity<ClubScheduleGetResponse> getClubSchedule(@PathVariable Long clubId, @Authenticated JwtUser jwtUser) {
        List<Event> events = clubService.getClubSchedules(clubId, jwtUser.id());

        List<ClubScheduleGetResponseInfo> schedules = events.stream()
                .map((event -> ClubScheduleGetResponseInfo.builder()
                        .eventId(event.getId())
                        .title(event.getTitle())
                        .startDateTime(event.getFormOpenDateTime())
                        .endDateTime(event.getFormCloseDateTime())
                        .manager(event.getManagerName())
                        .build()))
                .toList();

        return ResponseEntity.ok(new ClubScheduleGetResponse(schedules));
    }

}
