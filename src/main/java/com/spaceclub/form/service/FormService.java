package com.spaceclub.form.service;

import com.spaceclub.club.domain.ClubUser;
import com.spaceclub.club.repository.ClubRepository;
import com.spaceclub.club.repository.ClubUserRepository;
import com.spaceclub.event.domain.ApplicationStatus;
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
        Form form = formRepository.findById(event.getFormId()).orElseThrow(() -> new IllegalStateException("존재하지 않는 폼입니댜."));
        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalStateException("존재하지 않는 유저입니댜."));

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
    public void updateApplicationStatus(Long eventId, Long userId, ApplicationStatus status) {
        EventUser eventUser = eventUserRepository.findByEventIdAndUserId(eventId, userId).orElseThrow(() -> new IllegalStateException("해당 신청 내역이 없습니다."));
        Event event = validateEventAndForm(eventUser.getEventId());
        validateClubManager(event.getClubId(), userId);

        EventUser updatedEventUser = eventUser.updateStatus(status);
        eventUserRepository.save(updatedEventUser);
    }


    private Event validateEventAndForm(Long eventId) {
        Event event = validateEvent(eventId);
        if (event.getForm() == null) throw new IllegalStateException("폼이 없는 행사입니다.");

        return event;
    }

    private Event validateEvent(Long eventId) {
        return eventRepository.findById(eventId).orElseThrow(() -> new IllegalStateException("존재하지 않는 행사입니댜."));
    }

    private void validateClubManager(Long clubId, Long userId) {
        if (!clubRepository.existsById(clubId)) throw new IllegalStateException("존재하지 않는 클럽입니다.");
        if (!userRepository.existsById(userId)) throw new IllegalStateException("존재하지 않는 유저입니다.");

        ClubUser clubUser = clubUserRepository.findByClub_IdAndUser_Id(clubId, userId)
                .orElseThrow(() -> new IllegalStateException("클럽의 멤버가 아닙니다."));
        if (clubUser.isNotManager()) throw new IllegalStateException("관리자만 접근 가능합니다.");
    }


}
