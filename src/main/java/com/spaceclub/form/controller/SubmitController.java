package com.spaceclub.form.controller;

import com.spaceclub.form.controller.dto.FormSubmitGetResponse;
import com.spaceclub.form.controller.dto.FormSubmitUpdateRequest;
import com.spaceclub.form.service.SubmitService;
import com.spaceclub.form.service.vo.FormSubmitGetInfo;
import com.spaceclub.form.service.vo.FormSubmitUpdateInfo;
import com.spaceclub.global.Authenticated;
import com.spaceclub.global.jwt.vo.JwtUser;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
public class SubmitController {

    private final SubmitService submitService;

    @GetMapping(value = {"/{eventId}/forms/applications", "/{eventId}/forms/submit"})
    public ResponseEntity<FormSubmitGetResponse> getAll(
            @PathVariable Long eventId,
            Pageable pageable,
            @Authenticated JwtUser jwtUser
    ) {
        FormSubmitGetInfo formSubmitGetInfo = submitService.getAll(jwtUser.id(), eventId, pageable);

        return ResponseEntity.ok(FormSubmitGetResponse.from(formSubmitGetInfo));
    }

    @PatchMapping(value = {"/{eventId}/forms/applications-status", "/{eventId}/forms/submit"})
    public ResponseEntity<Void> updateStatus(
            @PathVariable Long eventId,
            @RequestBody FormSubmitUpdateRequest request,
            @Authenticated JwtUser jwtUser
    ) {
        FormSubmitUpdateInfo updateInfo = FormSubmitUpdateInfo.builder()
                .eventId(eventId)
                .formUserId(request.formUserId())
                .status(request.status())
                .userId(jwtUser.id())
                .build();

        submitService.updateStatus(updateInfo);

        return ResponseEntity.noContent().build();
    }

}
