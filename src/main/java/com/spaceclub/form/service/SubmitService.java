package com.spaceclub.form.service;

import com.spaceclub.club.util.ClubUserValidator;
import com.spaceclub.event.domain.Event;
import com.spaceclub.event.domain.EventUser;
import com.spaceclub.event.repository.EventUserRepository;
import com.spaceclub.event.service.EventProvider;
import com.spaceclub.event.service.util.EventValidator;
import com.spaceclub.form.domain.Form;
import com.spaceclub.form.domain.FormAnswer;
import com.spaceclub.form.repository.FormAnswerRepository;
import com.spaceclub.form.service.vo.FormSubmitGetInfo;
import com.spaceclub.form.service.vo.FormSubmitUpdateInfo;
import com.spaceclub.notification.mail.service.event.MailEvent;
import com.spaceclub.user.service.UserProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.spaceclub.event.EventExceptionMessage.EVENT_NOT_APPLIED;
import static com.spaceclub.event.EventExceptionMessage.EVENT_NOT_MANAGED;
import static com.spaceclub.event.domain.ParticipationStatus.CANCELED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SubmitService {

    private final FormAnswerRepository formAnswerRepository;

    private final EventUserRepository eventUserRepository;

    private final EventValidator eventValidator;

    private final ClubUserValidator clubUserValidator;

    private final EventProvider eventProvider;

    private final UserProvider userProvider;

    private final ApplicationEventPublisher applicationEventPublisher;

    public FormSubmitGetInfo getAll(Long userId, Long eventId, Pageable pageable) {
        Event event = eventProvider.getById(eventId);
        clubUserValidator.validateClubManager(event.getClubId(), userId);
        Form form = event.getForm();

        List<FormAnswer> formAnswers = formAnswerRepository.findByFormOption_Form_Id(form.getId());
        Page<EventUser> eventUser = eventUserRepository.findByEvent(event, pageable);

        return new FormSubmitGetInfo(form, formAnswers, eventUser);
    }

    @Transactional
    public void updateStatus(FormSubmitUpdateInfo updateInfo) {
        EventUser eventUser = eventUserRepository.findByEventIdAndUserId(updateInfo.eventId(), updateInfo.formUserId())
                .orElseThrow(() -> new IllegalStateException(EVENT_NOT_APPLIED.toString()));

        Event event = eventValidator.validateEventWithLock(eventUser.getEventId());
        if (event.isNotFormManaged()) throw new IllegalStateException(EVENT_NOT_MANAGED.toString());

        clubUserValidator.validateClubManager(event.getClubId(), updateInfo.userId());

        if (CANCELED.equals(updateInfo.status())) {
            eventProvider.minusParticipants(event, eventUser.getTicketCount());
        }

        EventUser updatedEventUser = eventUser.updateStatus(updateInfo.status());

        // 폼 상태 변경 시 메일 발송
        String userEmailAddress = userProvider.getProfile(eventUser.getUserId()).email();
        applicationEventPublisher.publishEvent(MailEvent.createMailEvent(userEmailAddress, event.getClubName(), event.getTitle(), updateInfo.status().toString()));

        eventUserRepository.save(updatedEventUser);
    }

}
