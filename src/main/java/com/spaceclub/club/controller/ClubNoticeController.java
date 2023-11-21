package com.spaceclub.club.controller;

import com.spaceclub.club.controller.dto.ClubNoticeCreateRequest;
import com.spaceclub.club.controller.dto.ClubNoticeGetResponse;
import com.spaceclub.club.controller.dto.ClubNoticeUpdateRequest;
import com.spaceclub.club.domain.ClubNotice;
import com.spaceclub.club.service.ClubNoticeService;
import com.spaceclub.club.service.vo.ClubNoticeDelete;
import com.spaceclub.club.service.vo.ClubNoticeUpdate;
import com.spaceclub.global.Authenticated;
import com.spaceclub.global.jwt.vo.JwtUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/clubs")
@RequiredArgsConstructor
public class ClubNoticeController {

    private final ClubNoticeService clubNoticeService;

    @PostMapping("/{clubId}/notices")
    public ResponseEntity<Void> createNotice(@PathVariable Long clubId,
                                             @RequestBody ClubNoticeCreateRequest request,
                                             @Authenticated JwtUser jwtUser) {
        clubNoticeService.createNotice(request.notice(), clubId, jwtUser.id());

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{clubId}/notices")
    public ResponseEntity<ClubNoticeGetResponse> getNotices(@PathVariable Long clubId, @Authenticated JwtUser jwtUser) {
        List<ClubNotice> notices = clubNoticeService.getNotices(clubId, jwtUser.id());

        ClubNoticeGetResponse response = new ClubNoticeGetResponse(notices);

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{clubId}/notices/{noticeId}")
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

        clubNoticeService.updateNotice(vo);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{clubId}/notices/{noticeId}")
    public ResponseEntity<Void> deleteNotice(@PathVariable Long clubId,
                                             @PathVariable Long noticeId,
                                             @Authenticated JwtUser jwtUser) {
        ClubNoticeDelete deleteVo = ClubNoticeDelete.builder()
                .clubId(clubId)
                .noticeId(noticeId)
                .userId(jwtUser.id())
                .build();

        clubNoticeService.deleteNotice(deleteVo);

        return ResponseEntity.noContent().build();
    }

}
