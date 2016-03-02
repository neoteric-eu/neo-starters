package com.neoteric.starter.auth.saasmgr.test;

import com.neoteric.starter.auth.saasmgr.SaasMgrAuthenticator;
import com.neoteric.starter.auth.saasmgr.SaasMgrPrincipal;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

import java.util.Arrays;

public class SaasMgrAuthenticationListener extends AbstractTestExecutionListener {

    @Override
    public void beforeTestClass(TestContext testContext) throws Exception {
        WithSaasMgrAuthentication annotation = testContext.getTestClass().getAnnotation(WithSaasMgrAuthentication.class);
        if (annotation == null) {
            return;
        }

        SaasMgrPrincipal saasDetails = new SaasMgrPrincipal.Builder()
                .customerId(annotation.customerId())
                .customerName(annotation.customerName())
                .email(annotation.email())
                .features(Arrays.asList(annotation.features()))
                .build();

        BeanDefinition bd = BeanDefinitionBuilder.genericBeanDefinition(TestSaasMgrAuthenticator.class)
                .addConstructorArgValue(saasDetails)
                .getBeanDefinition();
        bd.setPrimary(true);
        registerBeanDefinition(testContext, "saasMgrConnector", bd);
    }

    @Override
    public void afterTestClass(TestContext testContext) throws Exception {
        WithSaasMgrAuthentication annotation = testContext.getTestClass().getAnnotation(WithSaasMgrAuthentication.class);
        if (annotation == null) {
            return;
        }
        destroyBeanDefinition(testContext, "saasMgrConnector");

        BeanDefinition defaultConnector = BeanDefinitionBuilder.genericBeanDefinition(SaasMgrAuthenticator.class)
                .getBeanDefinition();
        defaultConnector.setFactoryBeanName("com.neoteric.starter.auth.SaasMgrSecurityAutoConfiguration");
        defaultConnector.setFactoryMethodName("saasMgrConnector");
        registerBeanDefinition(testContext, "saasMgrConnector", defaultConnector);
    }

    public static void registerBeanDefinition(TestContext testContext, String beanName, BeanDefinition beanDefinition) {
        DefaultListableBeanFactory listableBeanFactory = (DefaultListableBeanFactory)testContext.getApplicationContext().getAutowireCapableBeanFactory();
        listableBeanFactory.registerBeanDefinition(beanName, beanDefinition);
    }

    public static void destroyBeanDefinition(TestContext testContext, String beanName) {
        DefaultListableBeanFactory listableBeanFactory = (DefaultListableBeanFactory)testContext.getApplicationContext().getAutowireCapableBeanFactory();
        if (listableBeanFactory.containsBeanDefinition(beanName)) {
            listableBeanFactory.removeBeanDefinition(beanName);
        }
    }
}
