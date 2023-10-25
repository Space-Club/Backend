package com.spaceclub.club.controller;

import com.spaceclub.club.controller.dto.CreateClubRequest;
import com.spaceclub.club.domain.Club;
import com.spaceclub.club.service.ClubService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class ClubController {

    private final ClubService service;

    @PostMapping("/clubs")
    public ResponseEntity<String> createClub(@RequestBody CreateClubRequest request) {
        Club newClub = request.toEntity();
        Club createdClub = service.createClub(newClub);
        Long id = createdClub.getId();

        return ResponseEntity.created(URI.create("https://spaceclub.site/" + id)).build();
        return ResponseEntity.created(URI.create("api/v1/clubs/" + id)).build();
    }

    @GetMapping("/clubs/{clubId}")
    public ResponseEntity<String> getClub(@PathVariable Long clubId) {
        return ResponseEntity.ok("get club.");
    }

    @DeleteMapping("/clubs/{clubId}")
    public ResponseEntity<String> deleteClub(@PathVariable Long clubId) {
        return ResponseEntity.ok("delete club.");
    }

}
