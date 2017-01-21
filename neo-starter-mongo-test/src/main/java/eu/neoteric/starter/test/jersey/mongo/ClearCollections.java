package eu.neoteric.starter.test.jersey.mongo;

import java.lang.annotation.*;

@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface ClearCollections {

    /**
     * Clear collections after each test
     */
    String[] value();
    boolean drop() default false;
}
