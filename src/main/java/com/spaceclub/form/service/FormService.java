package com.spaceclub.form.service;

import com.spaceclub.event.domain.Event;
import com.spaceclub.event.domain.EventUser;
import com.spaceclub.event.repository.EventRepository;
import com.spaceclub.event.repository.EventUserRepository;
import com.spaceclub.form.domain.Form;
import com.spaceclub.form.domain.FormOptionUser;
import com.spaceclub.form.repository.FormOptionUserRepository;
import com.spaceclub.form.repository.FormRepository;
import com.spaceclub.form.service.vo.FormApplicationGetInfo;
import com.spaceclub.form.service.vo.FormCreate;
import com.spaceclub.form.service.vo.FormGet;
import com.spaceclub.user.domain.User;
import com.spaceclub.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FormService {

    private final EventRepository eventRepository;

    private final FormRepository formRepository;

    private final FormOptionUserRepository formOptionUserRepository;

    private final UserRepository userRepository;

    private final EventUserRepository eventUserRepository;

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
        Event event = validateEventAndForm(eventId);
        Form form = formRepository.findById(event.getFormId()).orElseThrow(() -> new IllegalStateException("존재하지 않는 폼입니댜."));
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalStateException("존재하지 않는 유저입니댜."));

        return FormGet.from(event, user, form);
    }

    public FormApplicationGetInfo getApplicationForms(Long userId, Long eventId, Pageable pageable) {
        Event event = validateEventAndForm(eventId);
        Form form = event.getForm();

        List<FormOptionUser> formOptionUsers = formOptionUserRepository.findByFormOption_Form_Id(form.getId());
        Page<EventUser> eventUser = eventUserRepository.findByEvent(event, pageable);

        return new FormApplicationGetInfo(form, formOptionUsers, eventUser);
    }

    private Event validateEventAndForm(Long eventId) {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new IllegalStateException("존재하지 않는 행사입니댜."));
        if (event.getForm() == null) throw new IllegalStateException("폼이 없는 행사입니다.");

        return event;
    }

}
