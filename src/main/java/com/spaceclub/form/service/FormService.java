package com.spaceclub.form.service;

import com.spaceclub.event.domain.Event;
import com.spaceclub.event.repository.EventRepository;
import com.spaceclub.form.controller.dto.FormApplicationGetResponse;
import com.spaceclub.form.controller.dto.FormGetResponse;
import com.spaceclub.form.domain.Form;
import com.spaceclub.form.repository.FormRepository;
import com.spaceclub.form.service.vo.FormCreate;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FormService {

    private final FormRepository formRepository;

    private final EventRepository eventRepository;

    @Transactional
    public Long createForm(FormCreate vo) {
        vo.form().addItems(vo.options());
        Form savedForm = formRepository.save(vo.form());

        Event event = eventRepository.findById(vo.eventId()).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 행사입니댜."));
        Event registeredForm = event.registerForm(savedForm);
        eventRepository.save(registeredForm);

        return vo.eventId();
    }

    public FormGetResponse getForm() {
        return FormGetResponse.builder().build();
    }

    @Transactional
    public void createApplicationForm() {
    }

    public List<FormApplicationGetResponse> getAllForms() {
        return List.of(FormApplicationGetResponse.builder().build());
    }


}
