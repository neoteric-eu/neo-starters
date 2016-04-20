package com.neoteric.starter.mvc.errorhandling.handler;

import lombok.Getter;
import lombok.ToString;
import org.springframework.context.ApplicationContext;

import java.util.Optional;
import java.util.Set;

@ToString
@Getter
public class RestExceptionHandlerRegistry {

    private final Set<ExceptionHandlerBinding> exceptionHandlerBindings;
    private ApplicationContext applicationContext;

    public RestExceptionHandlerRegistry(Set<ExceptionHandlerBinding> exceptionHandlerBindings) {
        this.exceptionHandlerBindings = exceptionHandlerBindings;
    }

    public Optional<ExceptionHandlerBinding> findBindingFor(Class<? extends Throwable> exceptionClass) {
        int currentDistance = Integer.MAX_VALUE;
        ExceptionHandlerBinding closestBinding = null;
        for (ExceptionHandlerBinding binding : exceptionHandlerBindings) {
            int tempDistance = getDistanceBetweenExceptions(exceptionClass, binding.getExceptionClass());
            if (tempDistance < currentDistance) {
                currentDistance = tempDistance;
                closestBinding = binding;
                if (currentDistance == 0) {
                    break;
                }
            }
        }
        return Optional.ofNullable(closestBinding);
    }

    private int getDistanceBetweenExceptions(Class<?> clazz, Class<?> mapperTypeClazz) {
        if (!mapperTypeClazz.isAssignableFrom(clazz)) {
            return Integer.MAX_VALUE;
        }

        Class<?> superClazz = clazz;
        int distance = 0;
        while (superClazz != mapperTypeClazz) {
            superClazz = superClazz.getSuperclass();
            distance++;
        }
        return distance;
    }
}
