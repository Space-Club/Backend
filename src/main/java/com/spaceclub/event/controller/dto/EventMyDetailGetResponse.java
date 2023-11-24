package com.spaceclub.event.controller.dto;

public record EventMyDetailGetResponse(
        Boolean isManager,
        Boolean isBookmarked
) {

}
