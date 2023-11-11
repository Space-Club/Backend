package com.spaceclub.club.controller;

import com.spaceclub.club.controller.dto.ClubCreateRequest;
import com.spaceclub.club.controller.dto.ClubEventGetResponse;
import com.spaceclub.club.controller.dto.ClubGetResponse;
import com.spaceclub.club.controller.dto.ClubNoticeCreateRequest;
import com.spaceclub.club.controller.dto.ClubNoticeGetResponse;
import com.spaceclub.club.controller.dto.ClubScheduleResponse;
import com.spaceclub.club.controller.dto.ClubUpdateRequest;
import com.spaceclub.club.controller.dto.ClubUserUpdateRequest;
import com.spaceclub.club.controller.dto.MemberGetResponse;
import com.spaceclub.club.domain.Club;
import com.spaceclub.club.domain.ClubUser;
import com.spaceclub.club.service.ClubService;
import com.spaceclub.club.service.vo.ClubUserUpdate;
import com.spaceclub.event.domain.Category;
import com.spaceclub.event.domain.Event;
import com.spaceclub.event.domain.EventInfo;
import com.spaceclub.event.domain.FormInfo;
import com.spaceclub.event.domain.TicketInfo;
import com.spaceclub.form.domain.Form;
import com.spaceclub.global.S3ImageUploader;
import com.spaceclub.global.dto.PageResponse;
import com.spaceclub.global.jwt.service.JwtService;
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
import java.time.LocalDateTime;
import java.util.List;

import static com.spaceclub.invite.controller.InviteController.INVITE_LINK_PREFIX;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ClubController {

    private final ClubService service;

    private final InviteService inviteService;

    private final S3ImageUploader uploader;

    private final JwtService jwtService;

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
    public ResponseEntity<ClubGetResponse> getClub(@PathVariable Long clubId, HttpServletRequest httpServletRequest) {
        Club club = service.getClub(clubId);
        Long userId = jwtService.verifyUserId(httpServletRequest);
        String inviteCode = inviteService.getInviteCode(clubId, userId);

        ClubGetResponse response = ClubGetResponse.from(club, INVITE_LINK_PREFIX + inviteCode);

        return ResponseEntity.ok(response);
    }

    @PostMapping(value = "/clubs/{clubId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> updateClub(@PathVariable Long clubId,
                                           @RequestPart(value = "request") ClubUpdateRequest request,
                                           @RequestPart(value = "logoImage", required = false) MultipartFile logoImage,
                                           HttpServletRequest httpServletRequest) {
        return ResponseEntity.noContent().build();
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

    @PostMapping("/clubs/{clubId}/notices")
    public ResponseEntity<Void> createNotice(@PathVariable Long clubId,
                                             @RequestBody ClubNoticeCreateRequest request,
                                             HttpServletRequest httpServletRequest) {

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/clubs/{clubId}/notices")
    public ResponseEntity<ClubNoticeGetResponse> getNotices(@PathVariable Long clubId, HttpServletRequest httpServletRequest) {
        ClubNoticeGetResponse response = new ClubNoticeGetResponse(List.of("notice"));

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/clubs/{clubId}/notices")
    public ResponseEntity<Void> updateNotice(@PathVariable Long clubId, HttpServletRequest httpServletRequest) {
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/clubs/{clubId}/notices")
    public ResponseEntity<Void> deleteNotice(@PathVariable Long clubId, HttpServletRequest httpServletRequest) {
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/clubs/{clubId}/schedules")
    public ResponseEntity<ClubScheduleResponse> getClubSchedule(@PathVariable Long clubId, HttpServletRequest httpServletRequest) {
        Event event = Event.builder()
                .id(1L)
                .category(Category.CLUB)
                .eventInfo(
                        EventInfo.builder()
                                .title("행사 제목")
                                .startDate(LocalDateTime.now())
                                .location("강남역")
                                .posterImageUrl("image.jpg")
                                .capacity(1)
                                .content("content")
                                .build()
                )
                .formInfo(
                        FormInfo.builder()
                                .formOpenDate(LocalDateTime.now())
                                .formCloseDate(LocalDateTime.now())
                                .build()
                )
                .ticketInfo(
                        TicketInfo.builder()
                                .cost(1000)
                                .maxTicketCount(10)
                                .build()
                )
                .club(
                        Club.builder()
                                .id(1L)
                                .name("클럽")
                                .logoImageUrl("logo image")
                                .coverImageUrl("cover image")
                                .info("club info")
                                .build()
                )
                .form(
                        Form.builder()
                                .id(1L)
                                .description("form description")
                                .build()
                )
                .build();
        ClubScheduleResponse response = new ClubScheduleResponse(List.of(event));

        return ResponseEntity.ok(response);
    }

}
