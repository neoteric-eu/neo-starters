package eu.neoteric.starter.test.jackson;

import eu.neoteric.starter.clock.TimeZoneAutoConfiguration;
import eu.neoteric.starter.jackson.StarterJacksonAfterAutoConfiguration;
import eu.neoteric.starter.jackson.StarterJacksonBeforeAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Combines all Jackson auto-configurations into single configuration handle
 */
@Configuration
@Import({TimeZoneAutoConfiguration.class,
        StarterJacksonBeforeAutoConfiguration.class,
        JacksonAutoConfiguration.class,
        StarterJacksonAfterAutoConfiguration.class})
public class TestJacksonConfiguration {
}