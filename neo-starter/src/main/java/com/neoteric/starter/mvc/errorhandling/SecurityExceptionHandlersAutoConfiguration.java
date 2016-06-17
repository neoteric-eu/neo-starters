package com.neoteric.starter.mvc.errorhandling;

import com.google.common.collect.ImmutableSet;
import com.neoteric.starter.mvc.errorhandling.handler.RestExceptionHandler;
import com.neoteric.starter.mvc.errorhandling.handlers.security.AccessDeniedExceptionHandler;
import com.neoteric.starter.mvc.errorhandling.handlers.security.AuthenticationExceptionHandler;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;

import java.util.Set;

@Configuration
@AutoConfigureBefore(StarterErrorHandlingAutoConfiguration.class)
@AutoConfigureAfter(ScannedExceptionHandlersAutoConfiguration.class)
@ConditionalOnClass({AccessDeniedException.class, AuthenticationException.class})
@ConditionalOnProperty(prefix = "neostarter.mvc.restErrorHandling",
        value = {"enabled", "defaultHandlersEnabled"},
        havingValue = "true", matchIfMissing = true)
@Import(SecurityExceptionHandlersAutoConfiguration.SecurityExceptionHandlersRegistrar.class)
public class SecurityExceptionHandlersAutoConfiguration {

    static class SecurityExceptionHandlersRegistrar extends AbstractExceptionHandlerRegistrar {

        static final ImmutableSet<Class<? extends RestExceptionHandler<? extends Exception>>> EXCEPTION_HANDLERS =
                ImmutableSet.of(AccessDeniedExceptionHandler.class,
                        AuthenticationExceptionHandler.class);

        @Override
        protected Set<Class<? extends RestExceptionHandler<? extends Exception>>> exceptionHandlerClasses(BeanDefinitionRegistry registry) {
            return EXCEPTION_HANDLERS;
        }
    }
}
