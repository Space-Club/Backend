package com.spaceclub.event.service;

import com.spaceclub.event.domain.ParticipationStatus;
import com.spaceclub.event.domain.Event;
import com.spaceclub.event.domain.EventUser;
import com.spaceclub.event.repository.EventUserRepository;
import com.spaceclub.event.service.vo.EventParticipationCreateInfo;
import com.spaceclub.form.domain.FormOptionUser;
import com.spaceclub.form.service.FormOptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.spaceclub.event.domain.ParticipationStatus.PENDING;
import static com.spaceclub.global.ExceptionCode.EVENT_ALREADY_APPLIED;
import static com.spaceclub.global.ExceptionCode.EVENT_NOT_APPLIED;

@Service
@Transactional
@RequiredArgsConstructor
public class ParticipationService {

    private final EventUserRepository eventUserRepository;

    private final FormOptionService formApplicationService;

    private final EventValidator eventValidator;

    public void apply(EventParticipationCreateInfo info) {
        Event event = eventValidator.validateEvent(info.eventId());

        eventValidator.validateEventTicketCount(event.getMaxTicketCount(), info.ticketCount());
        if (eventUserRepository.existsByEventIdAndUserId(info.eventId(), info.userId()))
            throw new IllegalArgumentException(EVENT_ALREADY_APPLIED.toString());

        for (FormOptionUser formOptionUser : info.formOptionUsers()) {
            formApplicationService.createFormOption(info.userId(), formOptionUser);
        }

        EventUser newEventUser = EventUser.builder()
                .userId(info.userId())
                .event(event)
                .status(PENDING)
                .ticketCount(info.ticketCount())
                .build();

        eventUserRepository.save(newEventUser);
    }

    public ParticipationStatus cancel(Long eventId, Long userId) {
        Event event = eventValidator.validateEvent(eventId);

        EventUser eventUser = eventUserRepository.findByEventIdAndUserId(eventId, userId)
                .orElseThrow(() -> new IllegalArgumentException(EVENT_NOT_APPLIED.toString()));

        EventUser updateEventUser = eventUser.setStatusByManaged(event.isFormManaged());

        eventUserRepository.save(updateEventUser);

        return updateEventUser.getStatus();
    }

}
