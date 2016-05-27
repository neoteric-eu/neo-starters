package com.neoteric.starter.test.clock;

import com.neoteric.starter.test.utils.TestContextHelper;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.support.AbstractTestExecutionListener;

import java.time.Clock;
import java.time.Instant;
import java.util.TimeZone;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.when;

public class FixedClockListener extends AbstractTestExecutionListener {

    @Override
    public void beforeTestClass(TestContext testContext) throws Exception {
        TestContextHelper contextHelper = new TestContextHelper(testContext);
        FixedClock classFixedClock = contextHelper.getTestClassAnnotation(FixedClock.class);
        if (classFixedClock == null) {
            return;
        }
        mockClock(contextHelper, classFixedClock);
    }

    @Override
    public void beforeTestMethod(TestContext testContext) throws Exception {
        TestContextHelper contextHelper = new TestContextHelper(testContext);
        FixedClock methodFixedClock = contextHelper.getTestMethodAnnotation(FixedClock.class);
        if (methodFixedClock == null) {
            return;
        }
        verifyClassAnnotation(contextHelper);
        mockClock(contextHelper, methodFixedClock);
    }

    @Override
    public void afterTestMethod(TestContext testContext) throws Exception {
        TestContextHelper contextHelper = new TestContextHelper(testContext);
        FixedClock methodFixedClock = contextHelper.getTestMethodAnnotation(FixedClock.class);
        if (methodFixedClock == null) {
            return;
        }
        verifyClassAnnotation(contextHelper);

        FixedClock classFixedClock = contextHelper.getTestClassAnnotation(FixedClock.class);
        mockClock(contextHelper, classFixedClock);
    }

    @Override
    public void afterTestClass(TestContext testContext) throws Exception {
        TestContextHelper contextHelper = new TestContextHelper(testContext);
        FixedClock annotation = contextHelper.getTestClassAnnotation(FixedClock.class);
        if (annotation == null) {
            return;
        }
        reset(contextHelper.getBean(Clock.class));
    }

    private void verifyClassAnnotation(TestContextHelper helper) {
        FixedClock classAnnotation = helper.getTestClassAnnotation(FixedClock.class);
        if (classAnnotation == null) {
            throw new IllegalStateException("@FixedClock class level annotation is missing.");
        }
    }
    private void mockClock(TestContextHelper helper, FixedClock fixedClock) {
        Instant instant = Instant.parse(fixedClock.value());
        Clock mockedClock = helper.getBean(Clock.class);
        when(mockedClock.instant()).thenReturn(instant);
        when(mockedClock.getZone()).thenReturn(TimeZone.getDefault().toZoneId());
    }
}