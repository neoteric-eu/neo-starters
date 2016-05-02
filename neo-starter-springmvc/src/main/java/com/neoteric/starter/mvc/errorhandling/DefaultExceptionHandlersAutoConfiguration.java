package com.neoteric.starter.mvc.errorhandling;

import com.google.common.collect.ImmutableSet;
import com.neoteric.starter.mvc.errorhandling.handler.RestExceptionHandler;
import com.neoteric.starter.mvc.errorhandling.handlers.common.*;
import com.neoteric.starter.mvc.errorhandling.handlers.custom.ResourceConflictExceptionHandler;
import com.neoteric.starter.mvc.errorhandling.handlers.custom.ResourceNotFoundExceptionHandler;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Conditional;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;

import java.util.Set;

@Configuration
@AutoConfigureBefore(StarterErrorHandlingAutoConfiguration.class)
@AutoConfigureAfter(ScannedExceptionHandlersAutoConfiguration.class)
@ConditionalOnProperty(prefix = "neostarter.mvc.restErrorHandling",
        value = {"enabled", "defaultHandlersEnabled"},
        havingValue = "true", matchIfMissing = true)
@Import({DefaultExceptionHandlersAutoConfiguration.DefaultExceptionHandlersRegistrar.class,
        DefaultExceptionHandlersAutoConfiguration.CustomExceptionHandlersRegistrar.class})
public class DefaultExceptionHandlersAutoConfiguration {

    static class DefaultExceptionHandlersRegistrar extends AbstractExceptionHandlerRegistrar {

        static final ImmutableSet<Class<? extends RestExceptionHandler<? extends Exception>>> EXCEPTION_HANDLERS =
                ImmutableSet.of(
                        FallbackExceptionHandler.class,
                        MethodArgumentNotValidExceptionHandler.class,
                        IllegalArgumentExceptionHandler.class,
                        NoHandlerFoundExceptionHandler.class,
                        HttpMessageNotReadableExceptionHandler.class);

        @Override
        protected Set<Class<? extends RestExceptionHandler<? extends Exception>>> exceptionHandlerClasses(BeanDefinitionRegistry registry) {
            return EXCEPTION_HANDLERS;
        }
    }

    static class CustomExceptionHandlersRegistrar extends AbstractExceptionHandlerRegistrar {

        static final ImmutableSet<Class<? extends RestExceptionHandler<? extends Exception>>> EXCEPTION_HANDLERS =
                ImmutableSet.of(ResourceConflictExceptionHandler.class,
                        ResourceNotFoundExceptionHandler.class);

        @Override
        protected Set<Class<? extends RestExceptionHandler<? extends Exception>>> exceptionHandlerClasses(BeanDefinitionRegistry registry) {
            return EXCEPTION_HANDLERS;
        }
    }
}
