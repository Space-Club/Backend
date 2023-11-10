package com.spaceclub.form.service.vo;

import com.spaceclub.event.domain.Event;
import com.spaceclub.form.domain.Form;
import lombok.Builder;

public record FormGet(
        String title,
        Form form

) {

    @Builder
    public FormGet {
    }

    public static FormGet from(Event event, Form form) {
        return FormGet.builder()
                .title(event.getTitle())
                .form(form)
                .build();
    }

}
