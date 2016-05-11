package com.neoteric.starter.test.clock;

import org.springframework.boot.test.autoconfigure.properties.PropertyMapping;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockReset;
import org.springframework.test.context.TestPropertySource;

import java.lang.annotation.*;
import java.time.Clock;

@Documented
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@MockBean(value = Clock.class, reset = MockReset.NONE)
public @interface FixedClock {
    String value() default "2010-01-10T10:00:00Z";
}