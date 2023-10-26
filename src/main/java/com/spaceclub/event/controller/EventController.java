package com.spaceclub.event.controller;

import com.spaceclub.event.controller.dto.CreateEventRequest;
import com.spaceclub.event.service.EventService;
import com.spaceclub.global.S3ImageUploader;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URI;

@Controller
@RequestMapping("/api/v1/event")
@RequiredArgsConstructor
public class EventController {

    private final EventService eventService;

    private final S3ImageUploader uploader;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> create(@RequestPart CreateEventRequest request, @RequestPart MultipartFile poster) throws IOException {

        String fileName = uploader.uploadImage(poster);
        Long eventId = eventService.create(request.toEntity(fileName));

        return ResponseEntity.created(URI.create("/api/v1/event/" + eventId)).build();
    }

}
