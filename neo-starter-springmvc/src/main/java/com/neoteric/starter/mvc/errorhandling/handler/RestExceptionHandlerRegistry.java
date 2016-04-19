package com.neoteric.starter.mvc.errorhandling.handler;

import lombok.Getter;
import lombok.ToString;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import java.util.Optional;
import java.util.Set;

@ToString
@Getter
public class RestExceptionHandlerRegistry implements ApplicationContextAware {

    private final Set<ExceptionHandlerBinding> exceptionHandlerBindings;
    private ApplicationContext applicationContext;

    public RestExceptionHandlerRegistry(Set<ExceptionHandlerBinding> exceptionHandlerBindings) {
        this.exceptionHandlerBindings = exceptionHandlerBindings;
    }

    public Optional<RestExceptionHandler> findMapperFor(Class<? extends Throwable> exceptionClass) {
        int currentDistance = Integer.MAX_VALUE;
        String closestExceptionHandlerBeanName = null;
        for (ExceptionHandlerBinding binding : exceptionHandlerBindings) {
            int tempDistance = getDistanceBetweenExceptions(exceptionClass, binding.getExceptionClass());
            if (tempDistance < currentDistance) {
                currentDistance = tempDistance;
                closestExceptionHandlerBeanName = binding.getExceptionHandlerBeanName();
                if (currentDistance == 0) {
                    break;
                }
            }
        }
        return Optional.ofNullable(applicationContext.getBean(closestExceptionHandlerBeanName, RestExceptionHandler.class));
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

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }
}
