package com.spaceclub.club.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.spaceclub.event.controller.dto.createRequest.ClubEventCreateRequest;
import com.spaceclub.event.controller.dto.createRequest.PromotionEventCreateRequest;
import com.spaceclub.event.controller.dto.createRequest.RecruitmentEventCreateRequest;
import com.spaceclub.event.controller.dto.createRequest.ShowEventCreateRequest;
import com.spaceclub.event.domain.Event;
import com.spaceclub.event.domain.EventCategory;

import static com.spaceclub.global.exception.GlobalExceptionCode.FAIL_DESERIALIZE;

public record EventCreateConverter(Long clubId, Event event) {

    private static final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    public static EventCreateConverter of(String request, EventCategory eventCategory) {
        return switch (eventCategory) {
            case SHOW -> getShowEvent(request, eventCategory);
            case PROMOTION -> getPromotionEvent(request, eventCategory);
            case RECRUITMENT -> getRecruitmentEvent(request, eventCategory);
            case CLUB -> getClubEvent(request, eventCategory);
        };
    }

    private static EventCreateConverter getShowEvent(String request, EventCategory eventCategory) {
        ShowEventCreateRequest showEventCreateRequest = deserialize(request, ShowEventCreateRequest.class);
        Event event = showEventCreateRequest.toEntity(eventCategory);
        Long clubId = showEventCreateRequest.clubId();
        return new EventCreateConverter(clubId, event);
    }

    private static EventCreateConverter getPromotionEvent(String request, EventCategory eventCategory) {
        PromotionEventCreateRequest promotionEventCreateRequest = deserialize(request, PromotionEventCreateRequest.class);
        Event event = promotionEventCreateRequest.toEntity(eventCategory);
        Long clubId = promotionEventCreateRequest.clubId();
        return new EventCreateConverter(clubId, event);
    }

    private static EventCreateConverter getRecruitmentEvent(String request, EventCategory eventCategory) {
        RecruitmentEventCreateRequest recruitmentEventCreateRequest = deserialize(request, RecruitmentEventCreateRequest.class);
        Event event = recruitmentEventCreateRequest.toEntity(eventCategory);
        Long clubId = recruitmentEventCreateRequest.clubId();
        return new EventCreateConverter(clubId, event);
    }

    private static EventCreateConverter getClubEvent(String request, EventCategory eventCategory) {
        ClubEventCreateRequest clubEventCreateRequest = deserialize(request, ClubEventCreateRequest.class);
        Event event = clubEventCreateRequest.toEntity(eventCategory);
        Long clubId = clubEventCreateRequest.clubId();
        return new EventCreateConverter(clubId, event);
    }

    private static <T> T deserialize(String data, Class<T> valueType) {
        try {
            return objectMapper.readValue(data, valueType);
        } catch (Exception e) {
            throw new RuntimeException(FAIL_DESERIALIZE.getMessage());
        }
    }

}
