package com.neoteric.starter.test.clock;

import com.neoteric.starter.clock.TimeZoneAutoConfiguration;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.RootBeanDefinition;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;

import java.time.Clock;
import java.time.Instant;

public class FixedClockRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata,
                                        BeanDefinitionRegistry registry) {
        String pattern = (String) importingClassMetadata
                .getAnnotationAttributes(FixedClock.class.getName()).get("value");
        BeanDefinition beanDefinition = new RootBeanDefinition(Clock.class);
        beanDefinition.setFactoryMethodName("fixed");
        beanDefinition.getConstructorArgumentValues().addIndexedArgumentValue(0, Instant.parse(pattern));
        beanDefinition.getConstructorArgumentValues().addIndexedArgumentValue(1, TimeZoneAutoConfiguration.UTC_ZONE);
        beanDefinition.setPrimary(true);
        registry.registerBeanDefinition("clock", beanDefinition);
    }
}
