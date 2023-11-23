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
    EVENT_TICKET_NOT_MANAGED("행사 티켓을 관리하지 않는 행사입니다"),
    TICKET_COUNT_REQUIRED("행사 티켓 매수는 필수입니다"),
    EXCEED_TICKET_COUNT("인 당 티켓 예매 가능 수를 초과하였습니다"),
    EVENT_NOT_MANAGED("폼이 없거나 관리모드를 설정하지 않은 폼입니다.");

    private final String message;

}
