package com.neoteric.starter.test;

import lombok.AllArgsConstructor;
import org.springframework.test.context.TestContext;

import java.lang.annotation.Annotation;

@AllArgsConstructor
public class TestContextHelper {

    private final TestContext testContext;

    public <T> T getBean(Class<T> beanClass) {
        return testContext.getApplicationContext().getBean(beanClass);
    }

    public <A extends Annotation> boolean testClassAnnotationNotPresent(Class<A> annotation) {
        return testContext.getTestClass().getAnnotation(annotation) == null;
    }

    public <A extends Annotation> A getTestClassAnnotation(Class<A> annotation) {
        return testContext.getTestClass().getAnnotation(annotation);
    }

    public <A extends Annotation> boolean testMethodAnnotationNotPresent(Class<A> annotation) {
        return testContext.getTestMethod().getAnnotation(annotation) == null;
    }

    public <A extends Annotation> A getTestMethodAnnotation(Class<A> annotation) {
        return testContext.getTestMethod().getAnnotation(annotation);
    }
}
