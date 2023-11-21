package com.spaceclub.form.service;

import com.spaceclub.club.domain.ClubUser;
import com.spaceclub.club.repository.ClubRepository;
import com.spaceclub.club.repository.ClubUserRepository;
import com.spaceclub.event.domain.Event;
import com.spaceclub.event.domain.EventUser;
import com.spaceclub.event.repository.EventRepository;
import com.spaceclub.event.repository.EventUserRepository;
import com.spaceclub.form.domain.Form;
import com.spaceclub.form.domain.FormOptionUser;
import com.spaceclub.form.repository.FormOptionUserRepository;
import com.spaceclub.form.repository.FormRepository;
import com.spaceclub.form.service.vo.FormApplicationGetInfo;
import com.spaceclub.form.service.vo.FormApplicationUpdateInfo;
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

import static com.spaceclub.global.ExceptionCode.CLUB_NOT_FOUND;
import static com.spaceclub.global.ExceptionCode.EVENT_NOT_APPLIED;
import static com.spaceclub.global.ExceptionCode.EVENT_NOT_FOUND;
import static com.spaceclub.global.ExceptionCode.EVENT_NOT_MANAGED;
import static com.spaceclub.global.ExceptionCode.FORM_NOT_FOUND;
import static com.spaceclub.global.ExceptionCode.NOT_CLUB_MEMBER;
import static com.spaceclub.global.ExceptionCode.UNAUTHORIZED;
import static com.spaceclub.global.ExceptionCode.USER_NOT_FOUND;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class FormService {

    private final EventRepository eventRepository;

    private final FormRepository formRepository;

    private final ClubRepository clubRepository;

    private final ClubUserRepository clubUserRepository;

    private final FormOptionUserRepository formOptionUserRepository;

    private final UserRepository userRepository;

    private final EventUserRepository eventUserRepository;

    @Transactional
    public Long createForm(FormCreate vo) {
        Event event = validateEvent(vo.eventId());
        validateClubManager(event.getClubId(), vo.userId());

        vo.form().addItems(vo.options());
        Form savedForm = formRepository.save(vo.form());

        Event registeredForm = event.registerForm(savedForm);
        eventRepository.save(registeredForm);

        return vo.eventId();
    }

    public FormGet getForm(Long userId, Long eventId) {
        Event event = validateEventAndForm(eventId);
        Form form = formRepository.findById(event.getFormId()).orElseThrow(() -> new IllegalStateException(FORM_NOT_FOUND.toString()));
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalStateException(USER_NOT_FOUND.toString()));

        return FormGet.from(event, user, form);
    }

    public FormApplicationGetInfo getApplicationForms(Long userId, Long eventId, Pageable pageable) {
        Event event = validateEventAndForm(eventId);
        validateClubManager(event.getClubId(), userId);
        Form form = event.getForm();

        List<FormOptionUser> formOptionUsers = formOptionUserRepository.findByFormOption_Form_Id(form.getId());
        Page<EventUser> eventUser = eventUserRepository.findByEvent(event, pageable);

        return new FormApplicationGetInfo(form, formOptionUsers, eventUser);
    }

    @Transactional
    public void updateApplicationStatus(FormApplicationUpdateInfo updateInfo) {
        EventUser eventUser = eventUserRepository.findByEventIdAndUserId(updateInfo.eventId(), updateInfo.formUserId())
                .orElseThrow(() -> new IllegalStateException(EVENT_NOT_APPLIED.toString()));
        Event event = validateEventAndForm(eventUser.getEventId());
        validateClubManager(event.getClubId(), updateInfo.userId());

        EventUser updatedEventUser = eventUser.updateStatus(updateInfo.status());
        eventUserRepository.save(updatedEventUser);
    }


    private Event validateEventAndForm(Long eventId) {
        Event event = validateEvent(eventId);
        if (event.isNotFormManaged()) throw new IllegalStateException(EVENT_NOT_MANAGED.toString());

        return event;
    }

    private Event validateEvent(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() -> new IllegalStateException(EVENT_NOT_FOUND.toString()));
    }

    private void validateClubManager(Long clubId, Long userId) {
        if (!clubRepository.existsById(clubId)) throw new IllegalStateException(CLUB_NOT_FOUND.toString());
        if (!userRepository.existsById(userId)) throw new IllegalStateException(USER_NOT_FOUND.toString());

        ClubUser clubUser = clubUserRepository.findByClub_IdAndUserId(clubId, userId)
                .orElseThrow(() -> new IllegalStateException(NOT_CLUB_MEMBER.toString()));
        if (clubUser.isNotManager()) throw new IllegalStateException(UNAUTHORIZED.toString());
    }


}
