package com.spaceclub.event;

import com.spaceclub.global.exception.ExceptionMessageInterface;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum EventExceptionMessage implements ExceptionMessageInterface {

    EVENT_NOT_FOUND("존재하지 않는 행사입니다"),
    EVENT_CATEGORY_NOT_ALLOWED("클럽을 제외한 카테고리의 행사만 조회 가능합니다"),
    EVENT_ALREADY_APPLIED("이미 신청한 행사입니다"),
    EVENT_NOT_APPLIED("신청한 이력이 없는 행사입니다"),
    TICKET_COUNT_REQUIRED("행사 티켓 매수는 필수입니다"),
    EXCEED_TICKET_COUNT("인 당 티켓 예매 가능 수를 초과하였습니다"),
    EXCEED_CAPACITY("정원을 초과하였습니다"),
    NOT_APPLICABLE_DATE("신청이 불가능한 기간의 행사입니다"),
    EVENT_NOT_MANAGED("폼이 없거나 관리모드를 설정하지 않은 폼입니다."),
    INVALID_EVENT_TITLE("제목은 1~30자 사이의 길이의 필수값입니다."),
    INVALID_EVENT_CONTENT("내용은 1~200자 사이의 길이입니다."),
    INVALID_EVENT_LOCATION("위치는 1~30자 사이의 길이입니다."),
    INVALID_EVENT_COST("비용은 1이상 100만원이하의 값입니다."),
    INVALID_EVENT_CAPACITY("정원은 1~999사이의 값입니다."),
    INVALID_EVENT_BANK_NAME("은행명은 1~20자 사이의 길이입니다."),
    INVALID_EVENT_BANK_ACCOUNT_NUMBER("은행 계좌번호는 1~30자 사이의 길이입니다."),
    INVALID_EVENT_MAX_TICKET_COUNT("인 당 예매 가능 수는 1이상 999이하의 값입니다."),
    EVENT_CATEGORY_NOT_NULL("행사 카테고리는 필수 값입니다."),
    POSTER_IMAGE_NOT_NULL("행사 포스터는 필수 값입니다."),
    INVALID_EVENT_USER("올바르지 못한 이벤트 유저 입니다"),
    INVALID_EVENT("올바르지 못한 이벤트 입니다");

    private final String message;

}
