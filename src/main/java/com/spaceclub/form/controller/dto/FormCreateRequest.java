package com.spaceclub.form.controller.dto;

import com.spaceclub.form.domain.Form;
import com.spaceclub.form.domain.FormOption;
import com.spaceclub.form.domain.FormOptionType;
import lombok.Builder;

import java.util.List;

public record FormCreateRequest(
        Long eventId,
        String description,
        boolean managed,
        List<FormCreateOptionRequest> options
) {

    @Builder
    public FormCreateRequest {
    }

    public record FormCreateOptionRequest(String title, FormOptionType type, boolean visible) {

    }

    public List<FormOption> toFormOptions() {
        return options.stream()
                .map(option -> FormOption.builder()
                        .title(option.title)
                        .type(option.type)
                        .visible(option.visible)
                        .build())
                .toList();
    }

    public Form toForm() {
        return Form.builder()
                .description(description)
                .managed(managed)
                .build();
    }

}
