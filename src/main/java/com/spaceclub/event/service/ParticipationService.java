package com.spaceclub.event.service;

import com.spaceclub.event.domain.Event;
import com.spaceclub.event.domain.EventUser;
import com.spaceclub.event.domain.ParticipationStatus;
import com.spaceclub.event.repository.EventUserRepository;
import com.spaceclub.event.service.util.EventValidator;
import com.spaceclub.event.service.vo.EventParticipationCreateInfo;
import com.spaceclub.form.domain.FormAnswer;
import com.spaceclub.form.service.FormOptionProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

import static com.spaceclub.event.EventExceptionMessage.APPLY_EXPIRED;
import static com.spaceclub.event.EventExceptionMessage.EVENT_ALREADY_APPLIED;
import static com.spaceclub.event.EventExceptionMessage.EVENT_NOT_APPLIED;
import static com.spaceclub.event.EventExceptionMessage.EXCEED_CAPACITY;
import static com.spaceclub.event.domain.EventCategory.SHOW;
import static com.spaceclub.event.domain.ParticipationStatus.CANCELED;
import static com.spaceclub.event.domain.ParticipationStatus.PENDING;

@Service
@Transactional
@RequiredArgsConstructor
public class ParticipationService {

    private final EventUserRepository eventUserRepository;

    private final FormOptionProvider formOptionProvider;

    private final EventProvider eventProvider;

    private final EventValidator eventValidator;

    public void apply(EventParticipationCreateInfo info) {
        Event event = eventValidator.validateEventWithLock(info.eventId());

        validateTicketCount(info.ticketCount(), event);

        Optional<EventUser> optionalEventUser = eventUserRepository.findByEventIdAndUserId(info.eventId(), info.userId());

        optionalEventUser.ifPresentOrElse(eventUser -> {
                    processByParticipationStatus(event.isFormed(), info, eventUser);
                    participateEvent(info, event);
                },
                () -> participateEvent(info, event));
    }

    private void validateTicketCount(Integer ticketCount, Event event) {
        if (SHOW.equals(event.getCategory())) {
            eventValidator.validateMaxEventTicketCount(event.getMaxTicketCount(), ticketCount);
        }
    }

    private void processByParticipationStatus(boolean isFormed, EventParticipationCreateInfo info, EventUser eventUser) {
        if (CANCELED.equals(eventUser.getStatus())) {
            eventUserRepository.deleteById(eventUser.getId());

            if (isFormed) {
                formOptionProvider.deleteFormAnswer(info.formAnswers(), info.userId());
            }
        } else {
            throw new IllegalArgumentException(EVENT_ALREADY_APPLIED.toString());
        }
    }

    private void participateEvent(EventParticipationCreateInfo info, Event event) {
        for (FormAnswer formAnswer : info.formAnswers()) {
            formOptionProvider.createFormOption(info.userId(), formAnswer);
        }

        int participants = addParticipants(info, event);

        Event updateEvent = event.registerParticipants(participants);
        eventProvider.update(updateEvent);

        createNewUser(info.userId(), info.ticketCount(), event);
    }

    public void createNewUser(Long userId, int ticketCount, Event event) {
        EventUser newEventUser = EventUser.builder()
                .userId(userId)
                .event(event)
                .status(PENDING)
                .ticketCount(ticketCount)
                .build();

        eventUserRepository.save(newEventUser);
    }

    private int addParticipants(EventParticipationCreateInfo info, Event event) {
        int participants = event.getParticipants() + info.ticketCount();
        validateCapacityAndCloseDate(event, participants);

        return participants;
    }

    private void validateCapacityAndCloseDate(Event event, int participants) {
        if (participants > event.getCapacity()) {
            throw new IllegalStateException(EXCEED_CAPACITY.toString());
        }

        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(event.getFormCloseDateTime())) {
            throw new IllegalStateException(APPLY_EXPIRED.toString());
        }
    }

    public ParticipationStatus cancel(Long eventId, Long userId) {
        Event event = eventValidator.validateEventWithLock(eventId);

        EventUser eventUser = eventUserRepository.findByEventIdAndUserId(eventId, userId)
                .orElseThrow(() -> new IllegalArgumentException(EVENT_NOT_APPLIED.toString()));

        if (event.isNotFormManaged()) {
            eventProvider.minusParticipants(event, eventUser.getTicketCount());
        }
        EventUser updateEventUser = eventUser.setStatusByManaged(event.isFormManaged());

        eventUserRepository.save(updateEventUser);

        return updateEventUser.getStatus();
    }

}
