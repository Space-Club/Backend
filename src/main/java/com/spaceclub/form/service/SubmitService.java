package com.spaceclub.form.service;

import com.spaceclub.club.service.ClubUserValidator;
import com.spaceclub.event.domain.Event;
import com.spaceclub.event.domain.EventUser;
import com.spaceclub.event.repository.EventUserRepository;
import com.spaceclub.event.service.EventValidator;
import com.spaceclub.form.domain.Form;
import com.spaceclub.form.domain.FormOptionUser;
import com.spaceclub.form.repository.FormOptionUserRepository;
import com.spaceclub.form.service.vo.FormSubmitGetInfo;
import com.spaceclub.form.service.vo.FormSubmitUpdateInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static com.spaceclub.global.ExceptionCode.EVENT_NOT_APPLIED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class SubmitService {

    private final FormOptionUserRepository formOptionUserRepository;

    private final EventUserRepository eventUserRepository;

    private final EventValidator eventValidator;

    private final ClubUserValidator clubUserValidator;

    public FormSubmitGetInfo getAll(Long userId, Long eventId, Pageable pageable) {
        Event event = eventValidator.validateEventAndForm(eventId);
        clubUserValidator.validateClubManager(event.getClubId(), userId);
        Form form = event.getForm();

        List<FormOptionUser> formOptionUsers = formOptionUserRepository.findByFormOption_Form_Id(form.getId());
        Page<EventUser> eventUser = eventUserRepository.findByEvent(event, pageable);

        return new FormSubmitGetInfo(form, formOptionUsers, eventUser);
    }

    @Transactional
    public void updateStatus(FormSubmitUpdateInfo updateInfo) {
        EventUser eventUser = eventUserRepository.findByEventIdAndUserId(updateInfo.eventId(), updateInfo.formUserId())
                .orElseThrow(() -> new IllegalStateException(EVENT_NOT_APPLIED.toString()));
        Event event = eventValidator.validateEventAndForm(eventUser.getEventId());
        clubUserValidator.validateClubManager(event.getClubId(), updateInfo.userId());

        EventUser updatedEventUser = eventUser.updateStatus(updateInfo.status());
        eventUserRepository.save(updatedEventUser);
    }

}
