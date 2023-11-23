package com.spaceclub.form.service;

import com.spaceclub.club.service.ClubUserValidator;
import com.spaceclub.event.domain.Event;
import com.spaceclub.event.repository.EventRepository;
import com.spaceclub.event.service.EventValidator;
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

    private final EventRepository eventRepository;

    private final FormRepository formRepository;

    private final ClubUserValidator clubUserValidator;

    private final EventValidator eventValidator;

    @Transactional
    public Long create(FormCreateInfo vo) {
        Event event = eventValidator.validateEvent(vo.eventId());
        clubUserValidator.validateClubManager(event.getClubId(), vo.userId());

        vo.form().addItems(vo.options());
        Form savedForm = formRepository.save(vo.form());

        Event registeredForm = event.registerForm(savedForm);
        eventRepository.save(registeredForm);

        return vo.eventId();
    }

    public FormGetInfo get(Long eventId) {
        Event event = eventValidator.validateEvent(eventId);
        Form form = formRepository.findById(event.getFormId())
                .orElseThrow(() -> new IllegalStateException(FORM_NOT_FOUND.toString()));

        return FormGetInfo.from(event, form);
    }

}
