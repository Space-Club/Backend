package com.spaceclub.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GlobalExceptionCode implements ExceptionMessageInterface {

    BAD_REQUEST("잘못된 요청입니다"),
    DESERIALIZE_FAILURE("JSON 데이터를 변환하는데 실패했습니다.");

    private final String message;

}
