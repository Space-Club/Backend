package com.spaceclub.global.exception.advice;

import com.spaceclub.global.exception.TokenException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;
import static org.springframework.http.HttpStatus.METHOD_NOT_ALLOWED;

@Slf4j
@RestControllerAdvice
public class ControllerAdvice {

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler
    public ErrorResponse customExceptionHandle(TokenException exception) {
        String exceptionName = exception.getClass().getSimpleName();
        log.info("토큰 관련 에러, exception name: {}", exceptionName);

        return new ErrorResponse(exceptionName, exception.getMessage());
    }

    @ResponseStatus(BAD_REQUEST)
    @ExceptionHandler({IllegalArgumentException.class, IllegalStateException.class})
    public ErrorResponse customExceptionHandle(RuntimeException exception) {
        String exceptionName = exception.getClass().getSimpleName();

        return new ErrorResponse(exceptionName, exception.getMessage());
    }

    @ResponseStatus(METHOD_NOT_ALLOWED)
    @ExceptionHandler({HttpRequestMethodNotSupportedException.class, HttpMessageNotReadableException.class})
    public ErrorResponse httpRequestMethodNotSupportedExceptionHandle(Exception exception) {
        String exceptionName = exception.getClass().getSimpleName();
        log.warn("잘못된 요청, exception name: {}", exceptionName);

        return new ErrorResponse(exceptionName, exception.getMessage());
    }

    @ResponseStatus(INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ErrorResponse exceptionHandler(Exception exception) {
        String exceptionName = exception.getClass().getSimpleName();
        log.info("서버 에러, exception name: {}", exceptionName);

        return new ErrorResponse(exceptionName, exception.getMessage());
    }

}
