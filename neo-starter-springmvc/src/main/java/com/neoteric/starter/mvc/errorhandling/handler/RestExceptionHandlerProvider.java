package com.neoteric.starter.mvc.errorhandling.handler;


import org.slf4j.event.Level;
import org.springframework.http.HttpStatus;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RestExceptionHandlerProvider {

    Level logLevel() default Level.ERROR;
    HttpStatus httpStatus() default HttpStatus.INTERNAL_SERVER_ERROR;
    boolean suppressStacktrace() default false;
    boolean suppressException() default false;

}