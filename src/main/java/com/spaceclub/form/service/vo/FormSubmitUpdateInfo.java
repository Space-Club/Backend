package com.spaceclub.form.service.vo;

import com.spaceclub.event.domain.ParticipationStatus;
import lombok.Builder;

public record FormSubmitUpdateInfo(
        Long eventId,
        Long formUserId,
        ParticipationStatus status,
        Long userId
) {

    @Builder
    public FormSubmitUpdateInfo {

    }

}
