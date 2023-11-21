package com.spaceclub.club.controller;

import com.spaceclub.club.controller.dto.ClubCreateRequest;
import com.spaceclub.club.controller.dto.ClubGetResponse;
import com.spaceclub.club.controller.dto.ClubUpdateRequest;
import com.spaceclub.club.domain.Club;
import com.spaceclub.club.service.ClubService;
import com.spaceclub.global.Authenticated;
import com.spaceclub.global.jwt.vo.JwtUser;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/clubs")
@RequiredArgsConstructor
public class ClubController {

    private final ClubService clubService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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

    @GetMapping("/{clubId}")
    public ResponseEntity<ClubGetResponse> getClub(@PathVariable Long clubId, @Authenticated JwtUser jwtUser) {
        Club club = clubService.getClub(clubId, jwtUser.id());

        ClubGetResponse response = ClubGetResponse.from(club);

        return ResponseEntity.ok(response);
    }

    @PatchMapping(value = "/{clubId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Void> updateClub(@PathVariable Long clubId,
                                           @RequestPart(value = "request", required = false) ClubUpdateRequest request,
                                           @RequestPart(value = "logoImage", required = false) MultipartFile logoImage,
                                           @RequestPart(value = "coverImage", required = false) MultipartFile coverImage,
                                           @Authenticated JwtUser jwtUser) {

        if (request == null) {
            request = new ClubUpdateRequest();
        }

        Club newClub = request.toEntity(clubId);
        clubService.updateClub(newClub, jwtUser.id(), logoImage, coverImage);

        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{clubId}")
    public ResponseEntity<String> deleteClub(@PathVariable Long clubId, @Authenticated JwtUser jwtUser) {
        clubService.deleteClub(clubId, jwtUser.id());
        return ResponseEntity.noContent().build();
    }

}
