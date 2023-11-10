package com.spaceclub.form.controller.dto;

import lombok.Builder;

import java.util.List;

@Builder
public record FormCreateRequest(
        Long eventId,
        String description,
        List<FormItemRequest> items
) {

    @Builder
    public record FormItemRequest(String name) {

    }

}
