package com.neoteric.starter.test.clock;


import java.lang.annotation.*;

@Documented
@Inherited
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface FixedClock {

    String value() default "2010-01-10T10:00:00Z";
}
