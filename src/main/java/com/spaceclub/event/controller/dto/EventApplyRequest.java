package com.spaceclub.event.controller.dto;

import lombok.Builder;

public record EventApplyRequest(
        Long eventId,
        Long userId
) {

    @Builder
    public EventApplyRequest {
    }

}
