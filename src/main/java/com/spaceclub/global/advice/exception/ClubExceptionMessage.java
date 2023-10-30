package com.spaceclub.global.advice.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ClubExceptionMessage implements ExceptionMessage {
    ;

    // TODO : 아래는 @Getter로 대체되니 삭제하고 사용해 주세요
    @Override
    public String getMessage() {
        return null;
    }
}
