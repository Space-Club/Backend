package com.spaceclub.event.service.vo;

import com.spaceclub.event.domain.Event;

public record EventGetInfo(
        Event event,
        boolean hasAlreadyApplied
) {

}
