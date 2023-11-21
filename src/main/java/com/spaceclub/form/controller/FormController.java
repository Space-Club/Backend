package com.spaceclub.form.controller;

import com.spaceclub.form.controller.dto.FormCreateRequest;
import com.spaceclub.form.controller.dto.FormGetResponse;
import com.spaceclub.form.service.FormService;
import com.spaceclub.form.service.vo.FormCreateInfo;
import com.spaceclub.form.service.vo.FormGetInfo;
import com.spaceclub.global.Authenticated;
import com.spaceclub.global.jwt.vo.JwtUser;
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

    @PostMapping("/forms")
    public ResponseEntity<Void> create(
            @RequestBody FormCreateRequest request,
            UriComponentsBuilder uriBuilder,
            @Authenticated JwtUser jwtUser
    ) {
        Long eventId = formService.create(FormCreateInfo.from(request, jwtUser.id()));

        URI location = uriBuilder
                .path("/api/v1/events/{id}")
                .buildAndExpand(eventId)
                .toUri();

        return ResponseEntity.created(location).build();
    }

    @GetMapping("/{eventId}/forms")
    public ResponseEntity<FormGetResponse> get(@PathVariable Long eventId, @Authenticated JwtUser jwtUser) {
        FormGetInfo formGetInfoVo = formService.get(eventId);

        return ResponseEntity.ok(FormGetResponse.from(formGetInfoVo));
    }

}
