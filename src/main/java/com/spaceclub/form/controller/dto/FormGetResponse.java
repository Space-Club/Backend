package com.spaceclub.form.controller.dto;

import lombok.Builder;

import java.util.List;

public record FormGetResponse(
        EventResponse event,
        FormResponse form
) {

    @Builder
    public FormGetResponse {
    }

    public record EventResponse(String title, String posterImageUrl) {

    }

    public record FormResponse(String description, List<FormItemResponse> items) {

    }

    public record FormItemResponse(Long id, String name) {

    }

}
