package com.spaceclub.form;

import com.spaceclub.global.exception.ExceptionMessageInterface;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum FormExceptionMessage implements ExceptionMessageInterface {

    FORM_NOT_FOUND("존재하지 않는 폼입니다"),
    FORM_OPTION_NOT_FOUND("존재하지 않는 폼 옵션 입니다"),
    FORM_ANSWER_NOT_FOUND("존재하지 않는 유저의 폼 답변입니다");

    private final String message;

}
