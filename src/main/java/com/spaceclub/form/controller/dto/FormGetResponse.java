package com.spaceclub.form.controller.dto;

import com.spaceclub.form.domain.FormOption;
import com.spaceclub.form.domain.FormOptionType;
import com.spaceclub.form.service.vo.FormGetInfo;
import lombok.Builder;

import java.util.List;

public record FormGetResponse(
        EventResponse event,
        FormResponse form
) {

    @Builder
    public FormGetResponse {
    }

    private record EventResponse(String title) {

    }

    private record FormItemResponse(Long id, String title, FormOptionType type) {

    }

    private record FormResponse(String description, List<FormItemResponse> options) {

    }

    public static FormGetResponse from(FormGetInfo vo) {
        return FormGetResponse.builder()
                .event(new EventResponse(vo.title()))
                .form(new FormResponse(vo.getFormDescription(), from(vo.getFormOptions())))
                .build();
    }

    private static List<FormItemResponse> from(List<FormOption> formOptions) {
        return formOptions.stream()
                .map(FormGetResponse::mapToFormItemResponse)
                .toList();
    }

    private static FormItemResponse mapToFormItemResponse(FormOption formOption) {
        return new FormItemResponse(formOption.getId(), formOption.getTitle(), formOption.getType());
    }

}
