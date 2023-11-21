package com.spaceclub.global;

import lombok.Getter;

public enum ExceptionCode {

    USER_NOT_FOUND("존재하지 않는 유저입니다"),
    CLUB_NOT_FOUND("존재하지 않는 클럽입니다"),
    NOTICE_NOT_FOUND("존재하지 않는 공지사항입니다"),
    EVENT_NOT_FOUND("존재하지 않는 행사입니다"),
    FORM_NOT_FOUND("존재하지 않는 폼입니다"),
    FORM_OPTION_NOT_FOUND("존재하지 않는 폼 옵션 입니다"),
    INVITE_NOT_FOUND("해당 초대코드를 보유한 클럽이 없습니다"),
    BOOKMARK_NOT_FOUND("존재하지 않는 북마크입니다"),
    NOT_CLUB_MEMBER("해당 클럽의 멤버가 아닙니다"),
    UNAUTHORIZED("권한이 없습니다"),
    CAN_NOT_WITHDRAW("마지막 관리자는 탈퇴가 불가합니다"),
    EVENT_CATEGORY_NOT_ALLOWED("클럽을 제외한 카테고리의 행사만 조회 가능합니다"),
    EVENT_ALREADY_APPLIED("이미 신청한 행사입니다"),
    EVENT_NOT_APPLIED("신청한 이력이 없는 행사입니다"),
    EVENT_TICKET_NOT_MANAGED("행사 티켓을 관리하지 않는 행사입니다"),
    TICKET_COUNT_REQUIRED("행사 티켓 매수는 필수입니다"),
    EXCEED_TICKET_COUNT("인 당 티켓 예매 가능 수를 초과하였습니다"),
    EVENT_WITHOUT_FORM("폼이 없는 행사입니다"),
    INVITE_EXPIRED("만료된 초대링크 입니다"),
    CLUB_ALREADY_JOINED("이미 해당 클럽에 가입되어 있습니다"),
    ALREADY_BOOKMARKED("이미 북마크한 이벤트입니다"),
    BAD_REQUEST("잘못된 요청입니다");

    @Getter
    private final String message;

    ExceptionCode(String message) {
        this.message = message;
    }

}
