package com.neoteric.starter.test;

import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@SpringBootTest
@AutoConfigureMockMvc
public @interface SpringBootMockMvcTest {

    @AliasFor(annotation = SpringBootTest.class)
    String[] value() default {};

    @AliasFor(annotation = SpringBootTest.class)
    String[] properties() default {};

    @AliasFor(annotation = SpringBootTest.class)
    Class<?>[] classes() default {};

    boolean print() default false;
}
