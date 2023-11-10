package com.spaceclub.form.controller;

import com.spaceclub.form.controller.dto.FormCreateRequest;
import com.spaceclub.form.service.FormService;
import com.spaceclub.global.jwt.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class FormController {

    private final FormService formService;

    private final JwtService jwtService;

    @PostMapping("/forms")
    public ResponseEntity<Void> createForm(@RequestBody FormCreateRequest request, UriComponentsBuilder uriBuilder, HttpServletRequest servletRequest) {
        Long userId = jwtService.verifyUserId(servletRequest);
        Long eventId = formService.createForm();

        URI location = uriBuilder
                .path("/api/v1/events/{id}")
                .buildAndExpand(eventId)
                .toUri();

        return ResponseEntity.created(location).build();
    }

}
