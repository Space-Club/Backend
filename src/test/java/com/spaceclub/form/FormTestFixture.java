package com.spaceclub.form;

import com.spaceclub.form.domain.Form;
import com.spaceclub.form.domain.FormOption;
import com.spaceclub.form.domain.FormOptionType;

import java.util.List;

public class FormTestFixture {

    public static Form form() {
        return Form.builder()
                .id(1L)
                .description("폼 정보")
                .managed(true)
                .build();
    }

    public static FormOption formOption1() {
        return FormOption.builder()
                .id(1L)
                .title("이름")
                .type(FormOptionType.TEXT)
                .visible(true)
                .build();
    }

    public static FormOption formOption2() {
        return FormOption.builder()
                .id(2L)
                .title("연락처")
                .type(FormOptionType.TEXT)
                .visible(true)
                .build();
    }

}
