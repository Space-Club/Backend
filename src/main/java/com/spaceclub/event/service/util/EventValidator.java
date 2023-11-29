package com.spaceclub.event.service.util;

import com.spaceclub.event.domain.Event;
import com.spaceclub.global.bad_word_filter.BadWordFilter;

import static com.spaceclub.event.EventExceptionMessage.EVENT_TICKET_NOT_MANAGED;
import static com.spaceclub.event.EventExceptionMessage.EXCEED_TICKET_COUNT;
import static com.spaceclub.event.EventExceptionMessage.TICKET_COUNT_REQUIRED;

public class EventValidator {

    public static void validateEvent(Event event) {
        BadWordFilter.filter(event.getTitle());
        BadWordFilter.filter(event.getContent());
        BadWordFilter.filter(event.getLocation());
        BadWordFilter.filter(event.getBankName());
        BadWordFilter.filter(event.getBankAccountNumber());
    }

    public static void validateEventTicketCount(Integer maxTicketCount, Integer ticketCount) {
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
