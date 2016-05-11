package com.neoteric.starter.test.clock;

import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

import java.time.Clock;
import java.time.Instant;
import java.util.TimeZone;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

public class FixedClockListener extends AbstractTestExecutionListener {

    private static final ThreadLocal<Instant> INSTANT_HOLDER = new ThreadLocal<>();

    @Override
    public void beforeTestClass(TestContext testContext) throws Exception {
        FixedClock annotation = testContext.getTestClass().getAnnotation(FixedClock.class);
        if (annotation == null) {
            return;
        }

        Clock mock = testContext.getApplicationContext().getBean(Clock.class);
        Instant instant = Instant.parse(annotation.value());
        INSTANT_HOLDER.set(instant);
        when(mock.instant()).thenReturn(instant);
        when(mock.getZone()).thenReturn(TimeZone.getDefault().toZoneId());
    }

    @Override
    public void beforeTestMethod(TestContext testContext) throws Exception {
        FixedClock annotation = testContext.getTestMethod().getAnnotation(FixedClock.class);
        if (annotation == null) {
            return;
        }
        Clock mock = testContext.getApplicationContext().getBean(Clock.class);
        Instant instant = Instant.parse(annotation.value());
        when(mock.instant()).thenReturn(instant);
        when(mock.getZone()).thenReturn(TimeZone.getDefault().toZoneId());
    }

    @Override
    public void afterTestMethod(TestContext testContext) throws Exception {
        FixedClock annotation = testContext.getTestMethod().getAnnotation(FixedClock.class);
        if (annotation == null) {
            return;
        }
        FixedClock classAnnotation = testContext.getTestClass().getAnnotation(FixedClock.class);
        if (classAnnotation == null) {
            throw new IllegalStateException("Can't have method annotated without specified FixedClock on class level");
        }

        Instant instant = INSTANT_HOLDER.get();
        Clock mock = testContext.getApplicationContext().getBean(Clock.class);
        when(mock.instant()).thenReturn(instant);
        when(mock.getZone()).thenReturn(TimeZone.getDefault().toZoneId());
    }

    @Override
    public void afterTestClass(TestContext testContext) throws Exception {
        FixedClock annotation = testContext.getTestClass().getAnnotation(FixedClock.class);
        if (annotation == null) {
            return;
        }
        Clock mock = testContext.getApplicationContext().getBean(Clock.class);
        reset(mock);
        INSTANT_HOLDER.remove();
    }
}