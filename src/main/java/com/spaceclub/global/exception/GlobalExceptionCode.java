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
    FAIL_FILE_UPLOAD("파일 업로드에 실패했습니다."),
    MAX_IMAGE_SIZE_EXCEEDED("이미지의 최대 크기를 초과했습니다"),
    INVALID_REQUEST("HTTP 요청(메서드)이 잘못되었습니다"),
    KAKAO_LOGOUT_FAIL("카카오 로그아웃에 실패했습니다"),
    KAKAO_UNLINK_FAIL("카카오 계정 연결 해제에 실패했습니다")
    ;

    private final String message;

}
