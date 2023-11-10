package com.spaceclub.form.controller.dto;

import lombok.Builder;

import java.util.List;

public record FormApplicationGetResponse(
        String username,
        String phoneNumber,
        List<FormApplicationItemGetResponse> items
) {

    @Builder
    public FormApplicationGetResponse {

    }

    public record FormApplicationItemGetResponse(String name, String content) {

    }

}
