package com.spaceclub.form.service.vo;

import com.spaceclub.form.domain.FormOption;
import com.spaceclub.form.domain.FormAnswer;

public record FormUserInfo(Long formOptionId, FormSubmitInfo submitInfo) {

    public static FormUserInfo from(FormOption formOption, FormAnswer formAnswer) {
        return new FormUserInfo(
                formOption.getId(),
                new FormSubmitInfo(
                        formAnswer.getUserId(),
                        formOption.getTitle(),
                        formAnswer.getContent()
                ));
    }

    public record FormSubmitInfo(Long userId, String title, String content) {

    }

}
