package com.spaceclub.global.log;

import org.aspectj.lang.annotation.Pointcut;

public class PointCuts {

    @Pointcut("execution(* com.spaceclub..controller..*.*(..)) && !execution(* com.spaceclub..controller.dto..*.*(..))")
    public void allControllers() {
    }

}
