package com.spaceclub.form.service.vo;

import com.spaceclub.event.domain.Event;
import com.spaceclub.form.domain.Form;
import com.spaceclub.form.domain.FormOption;
import com.spaceclub.user.domain.User;
import lombok.Builder;

import java.util.List;

public record FormGet(
        String title,
        User user,
        Form form
) {

    @Builder
    public FormGet {
    }

    public static FormGet from(Event event, User user, Form form) {
        return FormGet.builder()
                .title(event.getTitle())
                .user(user)
                .form(form)
                .build();
    }

    public String getUsername() {
        return user.getUsername();
    }

    public String getPhoneNumber() {
        return user.getPhoneNumber();
    }

    public String getFormDescription() {
        return form.getDescription();
    }

    public List<FormOption> getFormOptions() {
        return form.getOptions();
    }

}
