package com.spaceclub.form.controller;

import com.spaceclub.form.controller.dto.FormApplicationGetResponse;
import com.spaceclub.form.controller.dto.FormCreateRequest;
import com.spaceclub.form.controller.dto.FormGetResponse;
import com.spaceclub.form.service.FormService;
import com.spaceclub.form.service.vo.FormApplicationGetInfo;
import com.spaceclub.form.service.vo.FormCreate;
import com.spaceclub.form.service.vo.FormGet;
import com.spaceclub.global.jwt.service.JwtManager;
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

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class FormController {

    private final FormService formService;

    private final JwtManager jwtManager;

    @PostMapping("/forms")
    public ResponseEntity<Void> createForm(@RequestBody FormCreateRequest request, UriComponentsBuilder uriBuilder, HttpServletRequest servletRequest) {
        Long userId = jwtManager.verifyUserId(servletRequest);
        Long eventId = formService.createForm(FormCreate.from(request, userId));

        URI location = uriBuilder
                .path("/api/v1/events/{id}")
                .buildAndExpand(eventId)
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @GetMapping("/{eventId}/forms")
    public ResponseEntity<FormGetResponse> getFormItem(@PathVariable Long eventId, HttpServletRequest servletRequest) {
        Long userId = jwtManager.verifyUserId(servletRequest);
        FormGet formGetVo = formService.getForm(userId, eventId);

        return ResponseEntity.ok(FormGetResponse.from(formGetVo));
    }

    @GetMapping("/{eventId}/forms/applications")
    public ResponseEntity<FormApplicationGetResponse> getApplicationForms(@PathVariable Long eventId, HttpServletRequest servletRequest) {
        Long userId = jwtManager.verifyUserId(servletRequest);
        FormApplicationGetInfo formApplicationGetInfo = formService.getApplicationForms(userId, eventId);

        return ResponseEntity.ok(FormApplicationGetResponse.from(formApplicationGetInfo));
    }

}
