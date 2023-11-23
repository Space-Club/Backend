package com.spaceclub.club;

import com.spaceclub.global.exception.ExceptionMessageInterface;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ClubExceptionMessage implements ExceptionMessageInterface {

    CLUB_NOT_FOUND("존재하지 않는 클럽입니다"),
    NOTICE_NOT_FOUND("존재하지 않는 공지사항입니다"),
    CAN_NOT_SELF_DEGRADING("혼자 남은 관리자는 강등될 수 없습니다"),
    CLUB_ALREADY_JOINED("이미 해당 클럽에 가입되어 있습니다"),
    NOT_CLUB_MEMBER("해당 클럽의 멤버가 아닙니다"),
    UNAUTHORIZED("권한이 없습니다"),
    CAN_NOT_WITHDRAW("마지막 관리자는 탈퇴가 불가합니다");

    private final String message;

}
