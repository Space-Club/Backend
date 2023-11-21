package com.spaceclub.event.service;

import com.spaceclub.event.domain.Event;
import com.spaceclub.event.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import static com.spaceclub.global.ExceptionCode.EVENT_NOT_FOUND;
import static com.spaceclub.global.ExceptionCode.EVENT_NOT_MANAGED;
import static com.spaceclub.global.ExceptionCode.EVENT_TICKET_NOT_MANAGED;
import static com.spaceclub.global.ExceptionCode.EXCEED_TICKET_COUNT;
import static com.spaceclub.global.ExceptionCode.TICKET_COUNT_REQUIRED;

@Component
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EventValidator {

    private final EventRepository eventRepository;

    public Event validateEventAndForm(Long eventId) {
        Event event = validateEvent(eventId);
        if (event.isNotFormManaged()) throw new IllegalStateException(EVENT_NOT_MANAGED.toString());

        return event;
    }

    public Event validateEvent(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() -> new IllegalStateException(EVENT_NOT_FOUND.toString()));
    }

    public void validateEventTicketCount(Integer maxTicketCount, Integer ticketCount) {
        boolean notManageTicket = maxTicketCount == null && ticketCount != null;
        boolean mustTicketCount = maxTicketCount != null && ticketCount == null;
        boolean exceedTicketCount = maxTicketCount != null && ticketCount != null && maxTicketCount < ticketCount;

        if (notManageTicket) {
            throw new IllegalArgumentException(EVENT_TICKET_NOT_MANAGED.toString());
        }
        if (mustTicketCount) {
            throw new IllegalArgumentException(TICKET_COUNT_REQUIRED.toString());
        }
        if (exceedTicketCount)
            throw new IllegalArgumentException(EXCEED_TICKET_COUNT.toString());
    }

}
