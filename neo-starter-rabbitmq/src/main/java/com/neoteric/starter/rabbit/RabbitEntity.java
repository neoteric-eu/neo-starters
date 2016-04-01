package com.neoteric.starter.rabbit;

import java.lang.annotation.*;

@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface RabbitEntity {

    /**
     * Entity name
     */
    String value();
}
