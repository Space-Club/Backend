package com.spaceclub.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GlobalExceptionCode implements ExceptionMessageInterface {

    BAD_REQUEST("잘못된 요청입니다"),
    FAIL_DESERIALIZE("JSON 데이터를 변환하는데 실패했습니다"),
    INVALID_TOKEN_FORMAT("토큰 포멧이 잘못되었습니다"),
    INVALID_ACCESS_TOKEN("유효하지 않은 엑세스 토큰입니다"),
    INVALID_REFRESH_TOKEN("유효하지 않은 리프레시 토큰입니다"),
    INVALID_FILE_EXTENSION("유효한 파일 확장자가 아닙니다"),
    FAIL_FILE_UPLOAD("파일 업로드에 실패했습니다.");

    private final String message;

}
