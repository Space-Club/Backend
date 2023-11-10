package com.spaceclub.form.controller.dto;

import lombok.Builder;

import java.util.List;

public record FormCreateRequest(
        Long eventId,
        String description,
        List<FormItemRequest> items
) {

    @Builder
    public FormCreateRequest {
    }

    public record FormItemRequest(String name) {

    }

}
