package com.neoteric.starter.mvc.errorhandling.handler;

import lombok.Builder;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.event.Level;
import org.springframework.http.HttpStatus;

@Data
@Builder
public final class ExceptionHandlerBinding {

    private final String exceptionHandlerBeanName;
    private final Class<? extends Throwable> exceptionClass;
    private final Logger logger;
    private final Level logLevel;
    private final HttpStatus httpStatus;
    private final boolean suppressStacktrace;
    private final boolean suppressException;
}
