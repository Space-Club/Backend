package com.spaceclub.form.service.vo;

import com.spaceclub.event.domain.EventUser;
import com.spaceclub.form.domain.Form;
import com.spaceclub.form.domain.FormOptionUser;
import org.springframework.data.domain.Page;

import java.util.List;

public record FormApplicationGetInfo(
        Form form,
        List<FormOptionUser> formOptionUsers,
        Page<EventUser> eventUsers
) {

}
