package com.spaceclub.form.controller;

import com.spaceclub.form.controller.dto.FormApplicationGetResponse;
import com.spaceclub.form.controller.dto.FormApplicationStatusUpdateRequest;
import com.spaceclub.form.controller.dto.FormCreateRequest;
import com.spaceclub.form.controller.dto.FormGetResponse;
import com.spaceclub.form.service.FormService;
import com.spaceclub.form.service.vo.FormApplicationGetInfo;
import com.spaceclub.form.service.vo.FormCreate;
import com.spaceclub.form.service.vo.FormGet;
import com.spaceclub.global.Authenticated;
import com.spaceclub.global.jwt.vo.JwtUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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

    @PostMapping("/forms")
    public ResponseEntity<Void> createForm(
            @RequestBody FormCreateRequest request,
            UriComponentsBuilder uriBuilder,
            @Authenticated JwtUser jwtUser
    ) {
        Long eventId = formService.createForm(FormCreate.from(request, jwtUser.id()));

        URI location = uriBuilder
                .path("/api/v1/events/{id}")
                .buildAndExpand(eventId)
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @GetMapping("/{eventId}/forms")
    public ResponseEntity<FormGetResponse> getFormItem(@PathVariable Long eventId, @Authenticated JwtUser jwtUser) {
        FormGet formGetVo = formService.getForm(jwtUser.id(), eventId);

        return ResponseEntity.ok(FormGetResponse.from(formGetVo));
    }

    @GetMapping("/{eventId}/forms/applications")
    public ResponseEntity<FormApplicationGetResponse> getApplicationForms(
            @PathVariable Long eventId,
            Pageable pageable,
            @Authenticated JwtUser jwtUser
    ) {
        FormApplicationGetInfo formApplicationGetInfo = formService.getApplicationForms(jwtUser.id(), eventId, pageable);

        return ResponseEntity.ok(FormApplicationGetResponse.from(formApplicationGetInfo));
    }

    @PatchMapping("/{eventId}/forms/applications-status")
    public ResponseEntity<Void> updateApplicationStatus(@PathVariable Long eventId, @RequestBody FormApplicationStatusUpdateRequest request, HttpServletRequest servletRequest) {
        Long userId = jwtService.verifyUserId(servletRequest);
        formService.updateApplicationStatus(eventId, userId, request.status());

        return ResponseEntity.noContent().build();
    }

}
