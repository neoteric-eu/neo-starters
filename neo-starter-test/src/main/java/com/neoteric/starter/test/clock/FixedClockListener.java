package com.neoteric.starter.test.clock;

import com.neoteric.starter.test.StarterTestUtils;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

import java.time.Instant;

import static com.neoteric.starter.test.StarterTestProfiles.FIXED_CLOCK;

public class FixedClockListener extends AbstractTestExecutionListener {

    private static final ThreadLocal<Instant> INSTANT_HOLDER = new ThreadLocal<>();

    @Override
    public void beforeTestClass(TestContext testContext) throws Exception {
        if (StarterTestUtils.doesNotHaveActiveProfile(testContext, FIXED_CLOCK)) {
            return;
        }
        FixedClock annotation = testContext.getTestClass().getAnnotation(FixedClock.class);
        if (annotation == null) {
            throw new IllegalStateException("Test class with 'fixedClock' profile should be annotated with @FixedClock");
        }

        Instant instant = Instant.parse(annotation.value());
        INSTANT_HOLDER.set(instant);
        StaticClock.instant = instant;
    }

    @Override
    public void beforeTestMethod(TestContext testContext) throws Exception {
        if (StarterTestUtils.doesNotHaveActiveProfile(testContext, FIXED_CLOCK)) {
            return;
        }
        FixedClock annotation = testContext.getTestMethod().getAnnotation(FixedClock.class);
        if (annotation == null) {
            return;
        }
        StaticClock.instant = Instant.parse(annotation.value());
    }

    @Override
    public void afterTestMethod(TestContext testContext) throws Exception {
        if (StarterTestUtils.doesNotHaveActiveProfile(testContext, FIXED_CLOCK)) {
            return;
        }
        FixedClock annotation = testContext.getTestMethod().getAnnotation(FixedClock.class);
        if (annotation == null) {
            return;
        }
        StaticClock.instant = INSTANT_HOLDER.get();
    }

    @Override
    public void afterTestClass(TestContext testContext) throws Exception {
        if (StarterTestUtils.doesNotHaveActiveProfile(testContext, FIXED_CLOCK)) {
            return;
        }
        INSTANT_HOLDER.remove();
    }
}
