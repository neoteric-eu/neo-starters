package com.neoteric.starter.test.clock;

import org.springframework.context.annotation.Import;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Import(FixedClockRegistrar.class)
public @interface FixedClock {

    String value() default "2010-01-10T10:00:00Z";

}