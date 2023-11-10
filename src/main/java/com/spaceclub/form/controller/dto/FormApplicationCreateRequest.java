package com.spaceclub.form.controller.dto;

import lombok.Builder;

@Builder
public record FormApplicationCreateRequest(Long id, String name) {

}
