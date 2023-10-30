package com.spaceclub.global.dto;

public record ExceptionResponse(String errorCode, String message) {

        public static ExceptionResponse of(String errorCode, String message) {
            return new ExceptionResponse(errorCode, message);
        }

}
