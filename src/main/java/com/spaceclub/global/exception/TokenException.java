package com.spaceclub.global.exception;

import lombok.Getter;

@Getter
public class TokenException extends RuntimeException {

    private final String message;

    public TokenException(ExceptionMessageInterface exceptionCode) {
        this.message = exceptionCode.getMessage();
    }

}
