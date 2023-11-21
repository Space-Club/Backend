package com.spaceclub.event.service.vo;

import com.spaceclub.event.domain.Event;
import lombok.Builder;
import org.springframework.web.multipart.MultipartFile;

public record EventCreateInfo(Event event, Long clubId, Long userId, MultipartFile posterImage) {

    @Builder
    public EventCreateInfo {
    }

}
