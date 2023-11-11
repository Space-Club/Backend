package com.spaceclub.form.service;

import com.spaceclub.event.domain.Event;
import com.spaceclub.event.repository.EventRepository;
import com.spaceclub.form.controller.dto.FormApplicationGetResponse;
import com.spaceclub.form.domain.Form;
import com.spaceclub.form.repository.FormRepository;
import com.spaceclub.form.service.vo.FormCreate;
import com.spaceclub.form.service.vo.FormGet;
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

        Event event = eventRepository.findById(vo.eventId()).orElseThrow(() -> new IllegalStateException("존재하지 않는 행사입니댜."));
        Event registeredForm = event.registerForm(savedForm);
        eventRepository.save(registeredForm);

        return vo.eventId();
    }

    public FormGet getForm(Long userId, Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new IllegalStateException("존재하지 않는 행사입니댜."));
        if (event.getForm() == null) throw new IllegalStateException("폼이 없는 행사입니다.");
        Form form = formRepository.findById(event.getFormId()).orElseThrow(() -> new IllegalStateException("존재하지 않는 폼입니댜."));

        return FormGet.from(event, form);
    }

    @Transactional
    public void createApplicationForm() {
    }

    public List<FormApplicationGetResponse> getAllForms() {
        return List.of(FormApplicationGetResponse.builder().build());
    }

}
