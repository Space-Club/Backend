package com.spaceclub.form.service;

import com.spaceclub.club.util.ClubUserValidator;
import com.spaceclub.event.domain.Event;
import com.spaceclub.event.repository.EventRepository;
import com.spaceclub.event.service.EventProvider;
import com.spaceclub.event.service.util.EventValidator;
import com.spaceclub.form.domain.Form;
import com.spaceclub.form.repository.FormRepository;
import com.spaceclub.form.service.vo.FormCreateInfo;
import com.spaceclub.form.service.vo.FormGetInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.spaceclub.form.FormExceptionMessage.FORM_NOT_FOUND;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FormService {

    private final EventProvider eventProvider;

    private final FormRepository formRepository;

    private final ClubUserValidator clubUserValidator;

    @Transactional
    public Long create(FormCreateInfo vo) {
        Event event = eventProvider.getById(vo.eventId());
        clubUserValidator.validateClubManager(event.getClubId(), vo.userId());

        vo.form().addItems(vo.options());
        Form savedForm = formRepository.save(vo.form());

        Event formRegisteredEvent = event.registerForm(savedForm);
        eventProvider.save(formRegisteredEvent);

        return vo.eventId();
    }

    public FormGetInfo get(Long eventId) {
        Event event = eventProvider.getById(eventId);
        Form form = formRepository.findById(event.getFormId())
                .orElseThrow(() -> new IllegalStateException(FORM_NOT_FOUND.toString()));

        return FormGetInfo.from(event, form);
    }

}
