package com.spaceclub.global.exception.advice;

public record ErrorResponse(
        String exceptionName,
        String code
) {

}
