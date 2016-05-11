package com.neoteric.starter.test;

import lombok.AllArgsConstructor;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.test.context.TestContext;

import java.lang.annotation.Annotation;

@AllArgsConstructor
public class TestContextHelper {

    private final TestContext testContext;

    public <T> T getBean(Class<T> beanClass) {
        return testContext.getApplicationContext().getBean(beanClass);
    }

    public <T> T getBean(String beanName, Class<T> beanClass) {
        return testContext.getApplicationContext().getBean(beanName, beanClass);
    }

    public <A extends Annotation> boolean testClassAnnotationNotPresent(Class<A> annotation) {
        return AnnotationUtils.findAnnotation(testContext.getTestClass(), annotation) == null;
    }

    public <A extends Annotation> A getTestClassAnnotation(Class<A> annotation) {
        return AnnotationUtils.findAnnotation(testContext.getTestClass(), annotation);
    }

    public <A extends Annotation> boolean testMethodAnnotationNotPresent(Class<A> annotation) {
        return AnnotationUtils.findAnnotation(testContext.getTestMethod(), annotation) == null;
    }

    public <A extends Annotation> A getTestMethodAnnotation(Class<A> annotation) {
        return AnnotationUtils.findAnnotation(testContext.getTestMethod(), annotation);
    }

    public String getProperty(String property) {
        return testContext.getApplicationContext().getEnvironment().getProperty(property);
    }

}
