package com.neoteric.starter.test.jersey.jackson;

import com.neoteric.starter.jersey.clock.TimeZoneAutoConfiguration;
import com.neoteric.starter.jersey.jackson.StarterJacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@Import({TimeZoneAutoConfiguration.class, StarterJacksonAutoConfiguration.class, JacksonAutoConfiguration.class})
public class TestJacksonConfiguration {
}
