package com.spaceclub.club.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.spaceclub.event.controller.dto.updateRequest.ClubEventUpdateRequest;
import com.spaceclub.event.controller.dto.updateRequest.PromotionEventUpdateRequest;
import com.spaceclub.event.controller.dto.updateRequest.RecruitmentEventUpdateRequest;
import com.spaceclub.event.controller.dto.updateRequest.ShowEventUpdateRequest;
import com.spaceclub.event.domain.Event;
import com.spaceclub.event.domain.EventCategory;

import static com.spaceclub.global.exception.GlobalExceptionCode.FAIL_DESERIALIZE;

public record EventUpdateConverter(Event event) {

    private static final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    public static EventUpdateConverter of(String request, EventCategory eventCategory) {
        return switch (eventCategory) {
            case SHOW -> getShowEvent(request, eventCategory);
            case PROMOTION -> getPromotionEvent(request, eventCategory);
            case RECRUITMENT -> getRecruitmentEvent(request, eventCategory);
            case CLUB -> getClubEvent(request, eventCategory);
        };
    }

    private static EventUpdateConverter getShowEvent(String request, EventCategory eventCategory) {
        ShowEventUpdateRequest showEventUpdateRequest = deserialize(request, ShowEventUpdateRequest.class);
        Event event = showEventUpdateRequest.toEntity(eventCategory);
        return new EventUpdateConverter(event);
    }

    private static EventUpdateConverter getPromotionEvent(String request, EventCategory eventCategory) {
        PromotionEventUpdateRequest promotionEventUpdateRequest = deserialize(request, PromotionEventUpdateRequest.class);
        Event event = promotionEventUpdateRequest.toEntity(eventCategory);
        return new EventUpdateConverter(event);
    }

    private static EventUpdateConverter getRecruitmentEvent(String request, EventCategory eventCategory) {
        RecruitmentEventUpdateRequest recruitmentEventUpdateRequest = deserialize(request, RecruitmentEventUpdateRequest.class);
        Event event = recruitmentEventUpdateRequest.toEntity(eventCategory);
        return new EventUpdateConverter(event);
    }

    private static EventUpdateConverter getClubEvent(String request, EventCategory eventCategory) {
        ClubEventUpdateRequest clubEventUpdateRequest = deserialize(request, ClubEventUpdateRequest.class);
        Event event = clubEventUpdateRequest.toEntity(eventCategory);
        return new EventUpdateConverter(event);
    }

    private static <T> T deserialize(String data, Class<T> valueType) {
        try {
            return objectMapper.readValue(data, valueType);
        } catch (Exception e) {
            throw new IllegalStateException(FAIL_DESERIALIZE.toString());
        }
    }

}
