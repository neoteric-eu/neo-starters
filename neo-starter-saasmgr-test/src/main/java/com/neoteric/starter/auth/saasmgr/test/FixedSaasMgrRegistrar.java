package com.neoteric.starter.auth.saasmgr.test;

import com.neoteric.starter.auth.saasmgr.SaasMgrPrincipal;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import java.util.Arrays;
import java.util.Map;

public class FixedSaasMgrRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {

        Map<String, Object> annotationAttributes = importingClassMetadata
                .getAnnotationAttributes(FixedSaasMgrAuthentication.class.getName());

        SaasMgrPrincipal saasDetails = new SaasMgrPrincipal.Builder()
                .customerId((String)annotationAttributes.get("customerId"))
                .userId((String)annotationAttributes.get("userId"))
                .customerName((String)annotationAttributes.get("customerName"))
                .email((String)annotationAttributes.get("email"))
                .features(Arrays.asList((String[])annotationAttributes.get("features")))
                .build();

        BeanDefinition bd = BeanDefinitionBuilder.genericBeanDefinition(TestSaasMgrAuthenticator.class)
                .addConstructorArgValue(saasDetails)
                .getBeanDefinition();
        bd.setPrimary(true);

        registry.registerBeanDefinition("saasMgrConnector", bd);
    }
}
