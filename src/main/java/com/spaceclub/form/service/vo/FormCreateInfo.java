package com.spaceclub.form.service.vo;

import com.spaceclub.form.controller.dto.FormCreateRequest;
import com.spaceclub.form.domain.Form;
import com.spaceclub.form.domain.FormOption;
import lombok.Builder;

import java.util.List;

public record FormCreateInfo(
        Long userId,
        Long eventId,
        Form form,
        List<FormOption> options

) {

    @Builder
    public FormCreateInfo {
    }

    public static FormCreateInfo from(FormCreateRequest request, Long userId) {
        return FormCreateInfo.builder()
                .userId(userId)
                .eventId(request.eventId())
                .form(request.toForm())
                .options(request.toFormOptions())
                .build();
    }

}
