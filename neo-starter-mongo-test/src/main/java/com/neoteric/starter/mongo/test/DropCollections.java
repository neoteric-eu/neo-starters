package com.neoteric.starter.mongo.test;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface DropCollections {

    /**
     * Drop collections after each test
     */
    String[] value();
}
