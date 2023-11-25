package com.spaceclub.form;

import com.spaceclub.form.domain.Form;
import com.spaceclub.form.domain.FormOption;
import com.spaceclub.form.domain.FormOptionType;
import com.spaceclub.form.domain.FormAnswer;

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
                .build();
    }

    public static FormOption formOption2() {
        return FormOption.builder()
                .id(2L)
                .title("연락처")
                .type(FormOptionType.TEXT)
                .build();
    }

    public static FormAnswer formAnswerUser1() {
        return FormAnswer.builder()
                .id(1L)
                .formOption(formOption1())
                .userId(1L)
                .content("박가네")
                .build();
    }

    public static FormAnswer formAnswerUser2() {
        return FormAnswer.builder()
                .id(2L)
                .formOption(formOption2())
                .userId(1L)
                .content("010-1111-2222")
                .build();
    }

}
