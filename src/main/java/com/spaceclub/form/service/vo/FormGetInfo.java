package com.spaceclub.form.service.vo;

import com.spaceclub.event.domain.Event;
import com.spaceclub.form.domain.Form;
import com.spaceclub.form.domain.FormOption;
import lombok.Builder;

import java.util.List;

public record FormGetInfo(
        String title,
        Form form
) {

    @Builder
    public FormGetInfo {
    }

    public static FormGetInfo from(Event event, Form form) {
        return FormGetInfo.builder()
                .title(event.getTitle())
                .form(form)
                .build();
    }

    public String getFormDescription() {
        return form.getDescription();
    }

    public List<FormOption> getFormOptions() {
        return form.getOptions();
    }

}
