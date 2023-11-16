package com.spaceclub.event.service.vo;

import com.spaceclub.form.domain.FormOptionUser;
import lombok.Builder;

import java.util.List;

public record EventApplicationCreateInfo(
        Long userId,
        Long eventId,
        List<FormOptionUser> formOptionUsers,
        Integer ticketCount
) {

    @Builder
    public EventApplicationCreateInfo {

    }

}
