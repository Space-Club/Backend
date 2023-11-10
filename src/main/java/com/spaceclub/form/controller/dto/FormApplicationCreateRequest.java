package com.spaceclub.form.controller.dto;

import lombok.Builder;

public record FormApplicationCreateRequest(Long id, String name) {

    @Builder
    public FormApplicationCreateRequest {
    }

}
