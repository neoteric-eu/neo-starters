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

    HttpStatus httpStatus() default HttpStatus.INTERNAL_SERVER_ERROR;
    Level logLevel() default Level.ERROR;
    String applicationCode() default "";
    boolean suppressStackTrace() default false;
    boolean suppressException() default false;

}