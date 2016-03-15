package com.neoteric.starter.test.mongo;

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
