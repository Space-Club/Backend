package com.spaceclub.global.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

import java.lang.reflect.Method;

@Slf4j
public class CustomAsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

    @Override
    public void handleUncaughtException(Throwable ex, Method method, Object... params) {
        log.error("[비동기 메서드에서 예외 발생] 메서드 이름: {} | 예외 클래스: {} | 예외 메세지: {}",
                method.getName(),
                ex.getClass().getSimpleName(),
                ex.getMessage()
        );
    }

}
