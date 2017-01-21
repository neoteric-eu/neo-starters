package eu.neoteric.starter.mvc.errorhandling.handler;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.event.Level;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;

import java.beans.Introspector;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

@Data
@EqualsAndHashCode(of = "exceptionClass")
@Builder
public class ExceptionHandlerBinding {

    private final String exceptionHandlerBeanName;
    private final Class<?> exceptionHandlerClass;
    private final Class<? extends Exception> exceptionClass;
    private final Logger logger;
    private final Level logLevel;
    private final String cause;
    private final HttpStatus httpStatus;
    private final boolean suppressStacktrace;
    private final boolean suppressException;

    public static ExceptionHandlerBinding fromAnnotatedClass(Class<?> exceptionHandlerClass) {
        Assert.notNull(exceptionHandlerClass);
        RestExceptionHandlerProvider annotation = exceptionHandlerClass.getAnnotation(RestExceptionHandlerProvider.class);
        Assert.state(annotation != null, exceptionHandlerClass + " class in not annotated with @RestExceptionHandlerProvider");
        return builder()
                .exceptionHandlerClass(exceptionHandlerClass)
                .logger(LoggerFactory.getLogger(exceptionHandlerClass))
                .exceptionClass(getExceptionClass(exceptionHandlerClass))
                .exceptionHandlerBeanName(getHandlerBeanName(exceptionHandlerClass))
                .httpStatus(annotation.httpStatus())
                .cause(annotation.applicationCode())
                .logLevel(annotation.logLevel())
                .suppressStacktrace(annotation.suppressStackTrace())
                .suppressException(annotation.suppressException())
                .build();
    }

    private static String getHandlerBeanName(Class<?> exceptionHandlerClass) {
        String shortClassName = exceptionHandlerClass.getSimpleName();
        return Introspector.decapitalize(shortClassName);
    }

    @SuppressWarnings("unchecked")
    private static Class<? extends Exception> getExceptionClass(Class<?> clazz) {
        Type[] types = clazz.getGenericInterfaces();
        for (Type type : types) {
            if (type instanceof ParameterizedType && ((ParameterizedType) type).getRawType() == RestExceptionHandler.class) {
                return (Class<? extends Exception>) ((ParameterizedType) type).getActualTypeArguments()[0];
            }
        }
        throw new IllegalStateException(clazz + " has to implement RestExceptionHandler interface");
    }
}
