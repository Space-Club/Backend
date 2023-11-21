package com.spaceclub.form.service.vo;

import com.spaceclub.event.domain.ApplicationStatus;
import lombok.Builder;

public record FormApplicationUpdateInfo(
        Long eventId,
        Long formUserId,
        ApplicationStatus status,
        Long userId
) {

    @Builder
    public FormApplicationUpdateInfo {

    }

}
