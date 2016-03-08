package com.neoteric.starter.mongo.test;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface EmbeddedMongoTest {

    /**
     * Drop collections between tests
     */
    String [] dropCollections() default {};
}
