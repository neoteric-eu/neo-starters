package com.neoteric.starter.mongo.test;

import org.springframework.test.context.TestPropertySource;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@TestPropertySource(properties = {"spring.data.mongodb.host=localhost", "spring.data.mongodb.port=0"})
public @interface EmbeddedMongoTest {

    /**
     * Drop collections between tests
     */
    String [] dropCollections() default {};
}
