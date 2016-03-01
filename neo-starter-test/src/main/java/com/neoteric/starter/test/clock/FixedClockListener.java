package com.neoteric.starter.test.clock;

import com.neoteric.starter.test.TestBeanUtils;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

public class FixedClockListener extends AbstractTestExecutionListener {

    @Override
    public void beforeTestClass(TestContext testContext) throws Exception {
        FixedClock annotation = testContext.getTestClass().getAnnotation(FixedClock.class);
        if (annotation == null) {
            return;
        }

        BeanDefinition bd = BeanDefinitionBuilder.genericBeanDefinition(Clock.class)
                .setFactoryMethod("fixed")
                .addConstructorArgValue(Instant.parse(annotation.value()))
                .addConstructorArgValue(ZoneId.systemDefault()).getBeanDefinition();
        bd.setPrimary(true);

        TestBeanUtils.registerBeanDefinition(testContext, "testClock", bd);
    }


    @Override
    public void afterTestClass(TestContext testContext) throws Exception {
        FixedClock annotation = testContext.getTestClass().getAnnotation(FixedClock.class);
        if (annotation == null) {
            return;
        }
        TestBeanUtils.destroyBeanDefinition(testContext, "testClock");
    }
}
