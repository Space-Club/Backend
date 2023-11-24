package com.spaceclub.event.service;

import com.spaceclub.event.domain.Event;
import com.spaceclub.event.domain.EventUser;
import com.spaceclub.event.domain.ParticipationStatus;
import com.spaceclub.event.repository.EventUserRepository;
import com.spaceclub.event.service.vo.EventPageInfo;
import com.spaceclub.event.service.vo.EventParticipationCreateInfo;
import com.spaceclub.form.domain.FormOptionUser;
import com.spaceclub.form.service.FormOptionProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static com.spaceclub.event.EventExceptionMessage.EVENT_ALREADY_APPLIED;
import static com.spaceclub.event.EventExceptionMessage.EVENT_NOT_APPLIED;
import static com.spaceclub.event.domain.ParticipationStatus.CANCELED;
import static com.spaceclub.event.domain.ParticipationStatus.PENDING;
import static java.util.stream.Collectors.toMap;

@Service
@Transactional
@RequiredArgsConstructor
public class ParticipationService implements ParticipationProvider {

    private final EventUserRepository eventUserRepository;

    private final FormOptionProvider formOptionProvider;

    private final EventValidator eventValidator;

    public void apply(EventParticipationCreateInfo info) {
        Event event = eventValidator.validateEvent(info.eventId());

        eventValidator.validateEventTicketCount(event.getMaxTicketCount(), info.ticketCount());

        Optional<EventUser> optionalEventUser = eventUserRepository.findByEventIdAndUserId(info.eventId(), info.userId());

        eventAlreadyParticipated(info, optionalEventUser);

        for (FormOptionUser formOptionUser : info.formOptionUsers()) {
            formOptionProvider.createFormOption(info.userId(), formOptionUser);
        }

        EventUser newEventUser = EventUser.builder()
                .userId(info.userId())
                .event(event)
                .status(PENDING)
                .ticketCount(info.ticketCount())
                .build();

        eventUserRepository.save(newEventUser);
    }

    private void eventAlreadyParticipated(EventParticipationCreateInfo info, Optional<EventUser> optionalEventUser) {
        if (optionalEventUser.isPresent()) {
            EventUser eventUser = optionalEventUser.get();

            if (CANCELED.equals(eventUser.getStatus())) {
                eventUserRepository.deleteById(eventUser.getId());
                formOptionProvider.deleteFormOptionUser(info.formOptionUsers(), info.userId());
            } else {
                throw new IllegalArgumentException(EVENT_ALREADY_APPLIED.toString());
            }
        }
    }

    public ParticipationStatus cancel(Long eventId, Long userId) {
        Event event = eventValidator.validateEvent(eventId);

        EventUser eventUser = eventUserRepository.findByEventIdAndUserId(eventId, userId)
                .orElseThrow(() -> new IllegalArgumentException(EVENT_NOT_APPLIED.toString()));

        EventUser updateEventUser = eventUser.setStatusByManaged(event.isFormManaged());

        eventUserRepository.save(updateEventUser);

        return updateEventUser.getStatus();
    }

    @Override
    public Page<EventPageInfo> findAllEventPages(Long userId, Pageable pageable) {
        Page<Event> eventPages = eventUserRepository.findAllByUserId(userId, pageable);

        List<Long> eventIds = eventPages.stream()
                .map(Event::getId)
                .toList();

        Map<Long, EventUser> eventUsers = eventUserRepository.findAllByUserIdAndEvent_IdIn(userId, eventIds).stream()
                .collect(toMap(EventUser::getEventId, Function.identity()));

        List<EventPageInfo> eventPageInfos = eventPages.getContent().stream()
                .map(event -> EventPageInfo.from(event, eventUsers.get(event.getId())))
                .toList();

        return new PageImpl<>(eventPageInfos, eventPages.getPageable(), eventPages.getTotalElements());
    }

}
