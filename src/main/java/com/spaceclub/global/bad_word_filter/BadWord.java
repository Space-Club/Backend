package com.spaceclub.global.bad_word_filter;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Constraint(validatedBy = BadWordFilter.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BadWord {

    String message() default "비속어가 발견 되었습니다";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

}
