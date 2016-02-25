package com.neoteric.starter.jersey.validation;

import org.glassfish.jersey.server.validation.ValidationConfig;
import org.glassfish.jersey.server.validation.internal.InjectingConstraintValidatorFactory;
import org.hibernate.validator.parameternameprovider.ParanamerParameterNameProvider;

import javax.ws.rs.container.ResourceContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.ext.ContextResolver;

public class ValidationConfigurationProvider implements ContextResolver<ValidationConfig> {

    @Context
    private ResourceContext resourceContext;

    @Override
    public ValidationConfig getContext(final Class<?> type) {
        return new ValidationConfig()
                .constraintValidatorFactory(resourceContext.getResource(InjectingConstraintValidatorFactory.class))
                .parameterNameProvider(new ParanamerParameterNameProvider());
    }
}
