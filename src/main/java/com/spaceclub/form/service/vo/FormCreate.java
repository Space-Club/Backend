package com.spaceclub.form.service.vo;

import com.spaceclub.form.domain.Form;
import com.spaceclub.form.domain.FormOption;
import lombok.Builder;

import java.util.List;

public record FormCreate(
        Long userId,
        Long eventId,
        Form form,
        List<FormOption> options

) {

    @Builder
    public FormCreate {
    }

}
