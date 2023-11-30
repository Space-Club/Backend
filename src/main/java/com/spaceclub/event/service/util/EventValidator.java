package com.spaceclub.event.service.util;

import com.spaceclub.event.domain.Event;
import com.spaceclub.event.repository.EventRepository;
import com.spaceclub.global.bad_word_filter.BadWordFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


import static com.spaceclub.event.EventExceptionMessage.EVENT_NOT_FOUND;
import static com.spaceclub.event.EventExceptionMessage.EXCEED_TICKET_COUNT;
import static com.spaceclub.event.EventExceptionMessage.TICKET_COUNT_REQUIRED;

@Component
@RequiredArgsConstructor
public class EventValidator {

    private final EventRepository eventRepository;

    public static void validateEvent(Event event) {
        BadWordFilter.filter(event.getTitle());
        BadWordFilter.filter(event.getContent());
        BadWordFilter.filter(event.getLocation());
        BadWordFilter.filter(event.getBankName());
        BadWordFilter.filter(event.getBankAccountNumber());
    }

    public Event validateEventWithLock(Long eventId) {
        return eventRepository.findWithLockById(eventId).orElseThrow(() -> new IllegalStateException(EVENT_NOT_FOUND.toString()));
    }

    public void validateMaxEventTicketCount(Integer maxTicketCount, Integer ticketCount) {
        boolean mustTicketCount = ticketCount == null;
        boolean exceedTicketCount = ticketCount != null && maxTicketCount < ticketCount;

        if (mustTicketCount) {
            throw new IllegalArgumentException(TICKET_COUNT_REQUIRED.toString());
        }
        if (exceedTicketCount)
            throw new IllegalArgumentException(EXCEED_TICKET_COUNT.toString());
    }

}
