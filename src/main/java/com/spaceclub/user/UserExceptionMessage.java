package com.spaceclub.user;

import com.spaceclub.global.exception.ExceptionMessageInterface;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum UserExceptionMessage implements ExceptionMessageInterface {

    USER_NOT_FOUND("존재하지 않는 유저입니다"),
    ALREADY_BOOKMARKED("이미 북마크한 이벤트입니다"),
    BOOKMARK_NOT_FOUND("존재하지 않는 북마크입니다"),
    USER_CANNOT_WITHDRAW("탈퇴 할 수 없는 유저입니다"),
    USER_DELETED("삭제된 사용자 입니다");

    private final String message;

}
