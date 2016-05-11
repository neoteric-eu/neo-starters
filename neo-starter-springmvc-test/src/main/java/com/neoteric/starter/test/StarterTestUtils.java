package com.neoteric.starter.test;

import org.springframework.test.context.TestContext;

import java.lang.annotation.Annotation;
import java.time.Clock;
import java.util.Arrays;

public final class StarterTestUtils {

    private StarterTestUtils() {
    }

    public static boolean hasActiveProfile(TestContext testContext, String profile) {
        String[] activeProfiles = testContext.getApplicationContext().getEnvironment().getActiveProfiles();
        return Arrays.stream(activeProfiles).anyMatch(s -> s.equals(profile));
    }

    public static boolean doesNotHaveActiveProfile(TestContext testContext, String profile) {
        return !hasActiveProfile(testContext, profile);
    }
}
