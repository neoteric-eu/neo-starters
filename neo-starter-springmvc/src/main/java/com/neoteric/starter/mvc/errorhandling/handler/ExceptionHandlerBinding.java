package com.neoteric.starter.mvc.errorhandling.handler;

import lombok.Builder;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;

import java.beans.Introspector;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

@Data
@Builder
public final class ExceptionHandlerBinding {

    private final String exceptionHandlerBeanName;
    private final Class<?> exceptionHandlerClass;
    private final Class<? extends Throwable> exceptionClass;
    private final Logger logger;
    private final Level logLevel;
    private final HttpStatus httpStatus;
    private final boolean suppressStacktrace;
    private final boolean suppressException;

    public static ExceptionHandlerBinding fromAnnotatedClass(Class<?> exceptionHandlerClass) {
        RestExceptionHandlerProvider annotation = exceptionHandlerClass.getAnnotation(RestExceptionHandlerProvider.class);
        Assert.notNull(annotation, exceptionHandlerClass + " class in not annotated with @RestExceptionHandlerProvider");
        return builder()
                .exceptionHandlerClass(exceptionHandlerClass)
                .logger(LoggerFactory.getLogger(exceptionHandlerClass))
                .exceptionClass(getExceptionClass(exceptionHandlerClass))
                .exceptionHandlerBeanName(getHandlerBeanName(exceptionHandlerClass))
                .httpStatus(annotation.httpStatus())
                .logLevel(annotation.logLevel())
                .suppressStacktrace(annotation.suppressStacktrace())
                .suppressException(annotation.suppressException())
                .build();
    }

    private static String getHandlerBeanName(Class<?> exceptionHandlerClass) {
        String shortClassName = exceptionHandlerClass.getSimpleName();
        return Introspector.decapitalize(shortClassName);
    }

    private static Class<? extends Throwable> getExceptionClass(Class<?> clazz) {
        Type[] types = clazz.getGenericInterfaces();
        for (Type type : types) {
            if (type instanceof ParameterizedType && ((ParameterizedType) type).getRawType() == RestExceptionHandler.class) {
                //noinspection unchecked
                return (Class<? extends Throwable>) ((ParameterizedType) type).getActualTypeArguments()[0];
            }
        }
        //Won't get in here
        return null;
    }
}
