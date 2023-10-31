package com.spaceclub.club.controller;

import com.spaceclub.club.controller.dto.ClubCreateRequest;
import com.spaceclub.club.controller.dto.ClubEventGetResponse;
import com.spaceclub.club.controller.dto.ClubGetResponse;
import com.spaceclub.club.domain.Club;
import com.spaceclub.club.service.ClubService;
import com.spaceclub.event.domain.Event;
import com.spaceclub.global.S3ImageUploader;
import com.spaceclub.global.dto.PageResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ClubController {

    private final ClubService service;

    private final S3ImageUploader uploader;

    @PostMapping(value = "/clubs", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> createClub(@RequestPart(value = "request") ClubCreateRequest request,
                                             @RequestPart(value = "thumbnail") MultipartFile thumbnail) throws IOException {
        String thumbnailUrl = uploader.uploadClubThumbnail(thumbnail);
        Club newClub = request.toEntity(thumbnailUrl);
        Club createdClub = service.createClub(newClub);
        Long id = createdClub.getId();

        return ResponseEntity.created(URI.create("/api/v1/clubs/" + id)).build();
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

}
