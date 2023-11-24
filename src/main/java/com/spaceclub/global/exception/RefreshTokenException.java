package com.spaceclub.global.exception;

public class RefreshTokenException extends TokenException {

    public RefreshTokenException(ExceptionMessageInterface exceptionCode) {
        super(exceptionCode);
    }
}
