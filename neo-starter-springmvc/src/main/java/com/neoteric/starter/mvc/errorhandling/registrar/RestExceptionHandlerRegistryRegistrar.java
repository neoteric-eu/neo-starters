package com.neoteric.starter.mvc.errorhandling.registrar;

import com.google.common.collect.Sets;
import com.neoteric.starter.mvc.errorhandling.handler.RestExceptionHandlerRegistry;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

public class RestExceptionHandlerRegistryRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClass(RestExceptionHandlerRegistry.class);
        beanDefinition.setRole(BeanDefinition.ROLE_INFRASTRUCTURE);
        beanDefinition.getConstructorArgumentValues()
                .addGenericArgumentValue(Sets.newHashSet());
        // We don't need this one to be post processed otherwise it can cause a
        // cascade of bean instantiation that we would rather avoid.
        beanDefinition.setSynthetic(true);
        registry.registerBeanDefinition(RestExceptionHandlerRegistry.BEAN_NAME, beanDefinition);
    }
}
