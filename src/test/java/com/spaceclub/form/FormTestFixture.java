package com.spaceclub.form;

import com.spaceclub.form.domain.Form;
import com.spaceclub.form.domain.FormOption;
import com.spaceclub.form.domain.FormOptionType;
import com.spaceclub.form.domain.FormOptionUser;

import static com.spaceclub.user.UserTestFixture.user1;

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

    public static FormOptionUser formOptionUser1() {
        return FormOptionUser.builder()
                .id(1L)
                .formOption(formOption1())
                .user(user1())
                .content("박가네")
                .build();
    }

    public static FormOptionUser formOptionUser2() {
        return FormOptionUser.builder()
                .id(2L)
                .formOption(formOption2())
                .user(user1())
                .content("010-1111-2222")
                .build();
    }

}
