package com.spaceclub.form.service.vo;

import com.spaceclub.event.domain.EventUser;
import com.spaceclub.form.domain.Form;
import com.spaceclub.form.domain.FormAnswer;
import org.springframework.data.domain.Page;

import java.util.List;

public record FormSubmitGetInfo(
        Form form,
        List<FormAnswer> formAnswers,
        Page<EventUser> eventUsers
) {

}
