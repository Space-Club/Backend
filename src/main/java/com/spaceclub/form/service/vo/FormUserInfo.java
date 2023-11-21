package com.spaceclub.form.service.vo;

import com.spaceclub.form.domain.FormOption;
import com.spaceclub.form.domain.FormOptionUser;

public record FormUserInfo(Long formOptionId, FormSubmitInfo submitInfo) {

    public static FormUserInfo from(FormOption formOption, FormOptionUser formOptionUser) {
        return new FormUserInfo(
                formOption.getId(),
                new FormSubmitInfo(
                        formOptionUser.getUserId(),
                        formOption.getTitle(),
                        formOptionUser.getContent()
                ));
    }

    public record FormSubmitInfo(Long userId, String title, String content) {

    }

}
