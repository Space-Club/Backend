package com.spaceclub.form.controller;

import com.spaceclub.form.controller.dto.FormApplicationCreateRequest;
import com.spaceclub.form.controller.dto.FormApplicationGetResponse;
import com.spaceclub.form.controller.dto.FormCreateRequest;
import com.spaceclub.form.controller.dto.FormGetResponse;
import com.spaceclub.form.service.FormService;
import com.spaceclub.form.service.vo.FormCreate;
import com.spaceclub.global.jwt.service.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class FormController {

    private final FormService formService;

    private final JwtService jwtService;

    @PostMapping("/forms")
    public ResponseEntity<Void> createForm(@RequestBody FormCreateRequest request, UriComponentsBuilder uriBuilder, HttpServletRequest servletRequest) {
        Long userId = jwtService.verifyUserId(servletRequest);
        Long eventId = formService.createForm(FormCreate.from(request, userId));

        URI location = uriBuilder
                .path("/api/v1/events/{id}")
                .buildAndExpand(eventId)
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @GetMapping("/{eventId}/formItems")
    public ResponseEntity<FormGetResponse> getFormItem(@PathVariable Long eventId, HttpServletRequest servletRequest) {
        Long userId = jwtService.verifyUserId(servletRequest);
        FormGetResponse response = formService.getForm();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/forms/applications")
    public ResponseEntity<Void> createApplicationForm(@RequestBody List<FormApplicationCreateRequest> request, HttpServletRequest servletRequest) {
        Long userId = jwtService.verifyUserId(servletRequest);
        formService.createApplicationForm();

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{eventId}/forms/applications")
    public ResponseEntity<List<FormApplicationGetResponse>> getAllApplicationForm(@PathVariable Long eventId, HttpServletRequest servletRequest) {
        Long userId = jwtService.verifyUserId(servletRequest);
        List<FormApplicationGetResponse> response = formService.getAllForms();

        return ResponseEntity.ok(response);
    }

}
