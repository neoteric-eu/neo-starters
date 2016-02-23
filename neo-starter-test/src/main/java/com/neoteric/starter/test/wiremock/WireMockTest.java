package com.neoteric.starter.test.wiremock;


import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@TestPropertySource(properties = "ribbon.eureka.enabled=false")
@ContextConfiguration(classes = WireMockConfiguration.class)
public @interface WireMockTest {

    String[] value() default {};
}
