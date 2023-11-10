package com.spaceclub.form.controller.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record FormGetResponse(
        EventResponse event,
        FormResponse form
) {

    @Builder
    public record EventResponse(String title, String posterImageUrl) {

    }

    @Builder
    public record FormResponse(String description, List<FormItemResponse> items) {

    }

    @Builder
    public record FormItemResponse(Long id, String name) {

    }

}
