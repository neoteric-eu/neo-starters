package com.neoteric.starter.test.clock;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import java.time.Clock;
import java.time.Instant;
import java.util.TimeZone;

@Configuration
@Profile("fixedClock")
public class FixedClockConfiguration {

    @Value("${neostarter.test.clock}")
    private String fixedClock = FixedClock.DEFAULT;

    @Bean
    @Primary
    Clock clock() {
        return Clock.fixed(Instant.parse(fixedClock), TimeZone.getDefault().toZoneId());
    }
}