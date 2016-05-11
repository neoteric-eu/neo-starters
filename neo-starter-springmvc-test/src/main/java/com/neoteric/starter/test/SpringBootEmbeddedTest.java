package com.neoteric.starter.test;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public @interface SpringBootEmbeddedTest {

    @AliasFor(annotation = SpringBootTest.class)
    String[] value() default {};

    @AliasFor(annotation = SpringBootTest.class)
    String[] properties() default {};

    @AliasFor(annotation = SpringBootTest.class)
    Class<?>[] classes() default {};
}
