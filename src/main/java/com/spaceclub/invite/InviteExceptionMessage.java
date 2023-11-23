package com.spaceclub.invite;

import com.spaceclub.global.exception.ExceptionMessageInterface;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum InviteExceptionMessage implements ExceptionMessageInterface {

    INVITE_NOT_FOUND("해당 초대코드를 보유한 클럽이 없습니다"),
    INVITE_EXPIRED("만료된 초대링크 입니다");

    private final String message;

}
