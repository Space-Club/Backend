package com.spaceclub.event.service.vo;

import com.spaceclub.form.domain.FormOptionUser;
import lombok.Builder;

import java.util.List;

public record EventParticipationCreateInfo(
        Long userId,
        Long eventId,
        List<FormOptionUser> formOptionUsers,
        Integer ticketCount
) {

    @Builder
    public EventParticipationCreateInfo {

    }

}
