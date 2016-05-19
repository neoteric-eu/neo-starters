package com.neoteric.starter.test.jackson;

import com.neoteric.starter.clock.TimeZoneAutoConfiguration;
import com.neoteric.starter.jackson.StarterJacksonAfterAutoConfiguration;
import com.neoteric.starter.jackson.StarterJacksonBeforeAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Combines all Jackson auto-configurations into single configuration handle
 */
@Configuration
@Import({TimeZoneAutoConfiguration.class,
        StarterJacksonBeforeAutoConfiguration.class,
        StarterJacksonAfterAutoConfiguration.class,
        JacksonAutoConfiguration.class})
public class TestJacksonConfiguration {
}
