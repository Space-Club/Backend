package com.spaceclub.form.controller;

import lombok.Builder;

import java.util.List;

@Builder
public record FormApplicationGetResponse(
        String username,
        String phoneNumber,
        List<FormApplicationItemGetResponse> items
) {

    @Builder
    public record FormApplicationItemGetResponse(String name, String content) {

    }

}
