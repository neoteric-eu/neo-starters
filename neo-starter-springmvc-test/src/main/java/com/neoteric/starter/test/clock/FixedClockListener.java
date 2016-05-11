package com.neoteric.starter.test.clock;

import com.neoteric.starter.test.TestContextHelper;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

import java.time.Clock;
import java.time.Instant;
import java.util.TimeZone;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

public class FixedClockListener extends AbstractTestExecutionListener {

    /**
     * Used for keeping Clock's Instant state between test methods. Allows different
     * Clock representations for particular test method.
     */
    private static final ThreadLocal<Instant> INSTANT_HOLDER = new ThreadLocal<>();

    @Override
    public void beforeTestClass(TestContext testContext) throws Exception {
        TestContextHelper contextHelper = new TestContextHelper(testContext);
        FixedClock fixedClock = contextHelper.getTestClassAnnotation(FixedClock.class);
        if (fixedClock == null) {
            return;
        }
        Instant instant = Instant.parse(fixedClock.value());
        INSTANT_HOLDER.set(instant);
        mockClock(contextHelper, instant);
    }

    @Override
    public void beforeTestMethod(TestContext testContext) throws Exception {
        TestContextHelper contextHelper = new TestContextHelper(testContext);
        FixedClock annotation = contextHelper.getTestMethodAnnotation(FixedClock.class);
        if (annotation == null) {
            return;
        }
        verifyClassAnnotation(contextHelper);

        Instant instant = Instant.parse(annotation.value());
        mockClock(contextHelper, instant);
    }

    @Override
    public void afterTestMethod(TestContext testContext) throws Exception {
        TestContextHelper contextHelper = new TestContextHelper(testContext);
        FixedClock annotation = contextHelper.getTestMethodAnnotation(FixedClock.class);
        if (annotation == null) {
            return;
        }
        verifyClassAnnotation(contextHelper);

        Instant instant = INSTANT_HOLDER.get();
        mockClock(contextHelper, instant);
    }

    @Override
    public void afterTestClass(TestContext testContext) throws Exception {
        TestContextHelper contextHelper = new TestContextHelper(testContext);
        FixedClock annotation = contextHelper.getTestClassAnnotation(FixedClock.class);
        if (annotation == null) {
            return;
        }
        reset(contextHelper.getBean(Clock.class));
        INSTANT_HOLDER.remove();
    }

    private void verifyClassAnnotation(TestContextHelper helper) {
        FixedClock classAnnotation = helper.getTestClassAnnotation(FixedClock.class);
        if (classAnnotation == null) {
            throw new IllegalStateException("@FixedClock class level annotation is missing.");
        }
    }
    private void mockClock(TestContextHelper helper, Instant instant) {
        Clock mockedClock = helper.getBean(Clock.class);
        when(mockedClock.instant()).thenReturn(instant);
        when(mockedClock.getZone()).thenReturn(TimeZone.getDefault().toZoneId());
    }
}