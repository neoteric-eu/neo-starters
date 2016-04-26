package com.neoteric.starter.mvc.errorhandling.registrar;

import com.neoteric.starter.StarterConstants;
import com.neoteric.starter.mvc.errorhandling.handler.ExceptionHandlerBinding;
import com.neoteric.starter.mvc.errorhandling.handler.RestExceptionHandler;
import com.neoteric.starter.mvc.errorhandling.handler.RestExceptionHandlerRegistry;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.ConstructorArgumentValues;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Set;

@Slf4j
public abstract class AbstractExceptionHandlerRegistrar implements ImportBeanDefinitionRegistrar {

    private static final String DEFAULT = "Default";
    private static final String CUSTOM = "Custom";

    protected abstract Set<Class<? extends RestExceptionHandler<? extends Exception>>> exceptionHandlerClasses(BeanDefinitionRegistry registry);

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

        BeanDefinition registryBeanDefinition;
        try {
            registryBeanDefinition = registry.getBeanDefinition(RestExceptionHandlerRegistry.BEAN_NAME);
        } catch (NoSuchBeanDefinitionException e) {
            throw new IllegalStateException("No RestExceptionHandlerRegistry bean found.");
        }

        ConstructorArgumentValues.ValueHolder constructorArgument = registryBeanDefinition
                .getConstructorArgumentValues().getGenericArgumentValue(Set.class);

        Set<ExceptionHandlerBinding> bindings = (Set<ExceptionHandlerBinding>) constructorArgument.getValue();
        exceptionHandlerClasses(registry).forEach(handlerClass -> registerExceptionHandler(registry, bindings, handlerClass));
        constructorArgument.setValue(bindings);
    }

    private String handlerPrefix(Class<? extends RestExceptionHandler<? extends Exception>> exceptionHandlerClass) {
        return DefaultExceptionHandlersRegistrar.DEFAULT_EXCEPTION_HANDLERS.contains(exceptionHandlerClass) ? DEFAULT : CUSTOM;
    }

    private void registerExceptionHandler(BeanDefinitionRegistry registry, Set<ExceptionHandlerBinding> bindings,
                                          Class<? extends RestExceptionHandler<? extends Exception>> handlerClass) {
        ExceptionHandlerBinding binding = ExceptionHandlerBinding.fromAnnotatedClass(handlerClass);
        registry.registerBeanDefinition(binding.getExceptionHandlerBeanName(), getExceptionHandlerBeanDefinition(handlerClass));
        bindings.add(binding);
        LOG.debug("{}{} {} [status: {}, level: {}, stackTrace: {}, exception: {}] registered.",
                StarterConstants.LOG_PREFIX, handlerPrefix(handlerClass), handlerClass.getSimpleName(), binding.getHttpStatus(),
                binding.getLogLevel(), binding.isSuppressStacktrace(), binding.isSuppressException());
    }

    private static BeanDefinition getExceptionHandlerBeanDefinition(Class<?> exceptionHandlerClass) {
        return BeanDefinitionBuilder
                .genericBeanDefinition(exceptionHandlerClass)
                .setScope(BeanDefinition.SCOPE_SINGLETON)
                .setRole(BeanDefinition.ROLE_INFRASTRUCTURE)
                .setAutowireMode(AutowireCapableBeanFactory.AUTOWIRE_AUTODETECT)
                .getBeanDefinition();
    }
}
