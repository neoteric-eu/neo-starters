package com.neoteric.starter.test;

import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.test.context.TestContext;

public final class TestBeanUtils {

    private TestBeanUtils() {
    }

    public static void registerSingleton(TestContext testContext, String singletonName, Object singletonObject) {
        DefaultListableBeanFactory listableBeanFactory = (DefaultListableBeanFactory)testContext.getApplicationContext().getAutowireCapableBeanFactory();
        listableBeanFactory.registerSingleton(singletonName, singletonObject);
    }

    public static void destroySingleton(TestContext testContext, String singletonName) {
        DefaultListableBeanFactory listableBeanFactory = (DefaultListableBeanFactory)testContext.getApplicationContext().getAutowireCapableBeanFactory();
        listableBeanFactory.destroySingleton(singletonName);
    }
}
