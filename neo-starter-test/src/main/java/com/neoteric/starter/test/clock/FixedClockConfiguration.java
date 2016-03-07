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
@Profile("testClock")
public class FixedClockConfiguration {

    @Value("${neostarter.test.clock:2010-01-10T10:00:00Z}")
    private String fixedClock;

    @Bean
    @Primary
    Clock clock() {
        return Clock.fixed(Instant.parse(fixedClock), TimeZone.getDefault().toZoneId());
    }
}