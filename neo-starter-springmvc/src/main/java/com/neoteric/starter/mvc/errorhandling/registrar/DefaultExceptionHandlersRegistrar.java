package com.neoteric.starter.mvc.errorhandling.registrar;

import com.google.common.collect.ImmutableSet;
import com.neoteric.starter.mvc.errorhandling.handler.RestExceptionHandler;
import com.neoteric.starter.mvc.errorhandling.handler.common.FallbackExceptionHandler;
import com.neoteric.starter.mvc.errorhandling.handler.common.HttpMessageNotReadableExceptionHandler;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;

import java.util.Set;

public class DefaultExceptionHandlersRegistrar extends AbstractExceptionHandlerRegistrar {

    static final ImmutableSet<Class<? extends RestExceptionHandler<? extends Exception>>> DEFAULT_EXCEPTION_HANDLERS =
            ImmutableSet.of(FallbackExceptionHandler.class,
                    HttpMessageNotReadableExceptionHandler.class);

    @Override
    protected Set<Class<? extends RestExceptionHandler<? extends Exception>>> exceptionHandlerClasses(BeanDefinitionRegistry registry) {
        return DEFAULT_EXCEPTION_HANDLERS;
    }
}