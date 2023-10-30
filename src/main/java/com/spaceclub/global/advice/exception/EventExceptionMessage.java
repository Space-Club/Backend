package com.spaceclub.global.advice.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EventExceptionMessage implements ExceptionMessage {

    INVALID_EVENT_TITLE("행사 제목은 30자 이하의 필수값입니다."),
    INVALID_EVENT_CONTENT("행사 내용은 200자 이하의 필수값입니다."),
    NULL_EVENT_START_DATE("행사 시작 날짜는 필수값입니다."),
    INVALID_EVENT_LOCATION("행사 장소는 30자 이하의 필수값입니다."),
    INVALID_EVENT_CAPACITY("행사 정원은 1이상 999이하의 값입니다."),
    NULL_EVENT_POSTER("행사 포스터는 필수값입니다."),
    INVALID_EVENT_BANK_NAME("은행 명은 20자 이하의 값입니다."),
    INVALID_EVENT_ACCOUNT_NUMBER("은행 계좌번호는 30자 이하의 값입니다."),
    INVALID_EVENT_MAX_TICKET_COUNT("인 당 예매 가능 수는 1이상 999이하의 값입니다."),
    INVALID_EVENT_COST("비용은 1이상 100만원이하의 값입니다."),
    NULL_EVENT_FORM_OPEN_DATE("폼 신청 시작 날짜와 시간은 필수값입니다."),
    NULL_EVENT_FORM_CLOSE_DATE("폼 신청 마감 날짜와 시간은 필수값입니다."),
    NULL_EVENT_CATEGORY("행사 카테고리는 필수 값입니다."),
    ;

    private final String message;

}
