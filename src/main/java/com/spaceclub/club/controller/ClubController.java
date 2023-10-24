package com.spaceclub.club.controller;

import com.spaceclub.club.controller.dto.CreateClubRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.net.URISyntaxException;

@RestController
@RequestMapping("/api/v1")
public class ClubController {

    @PostMapping("/club")
    public ResponseEntity<String> createClub(@RequestBody CreateClubRequest request) throws URISyntaxException {
        return ResponseEntity.created(new URI("https://spaceclub.site/1")).build();
    }

    @GetMapping("/club/{clubId}")
    public ResponseEntity<String> getClub(@PathVariable Long clubId) {
        return ResponseEntity.ok("get club.");
    }

    @DeleteMapping("/club/{clubId}")
    public ResponseEntity<String> deleteClub(@PathVariable Long clubId) {
        return ResponseEntity.ok("delete club.");
    }

}
