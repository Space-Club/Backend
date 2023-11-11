package com.spaceclub.form.service.vo;

import com.spaceclub.form.controller.dto.FormCreateRequest;
import com.spaceclub.form.domain.Form;
import com.spaceclub.form.domain.FormOption;
import lombok.Builder;

import java.util.List;

public record FormCreate(
        Long userId,
        Long eventId,
        Form form,
        List<FormOption> options

) {

    @Builder
    public FormCreate {
    }

    public static FormCreate from(FormCreateRequest request, Long userId) {
        return FormCreate.builder()
                .userId(userId)
                .eventId(request.eventId())
                .form(request.toForm())
                .options(request.toFormOptions())
                .build();
    }

}
