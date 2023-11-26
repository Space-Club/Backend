package com.spaceclub.event.service.vo;

import com.spaceclub.form.domain.FormAnswer;
import lombok.Builder;

import java.util.List;

public record EventParticipationCreateInfo(
        Long userId,
        Long eventId,
        List<FormAnswer> formAnswers,
        Integer ticketCount
) {

    @Builder
    public EventParticipationCreateInfo {

    }

}
