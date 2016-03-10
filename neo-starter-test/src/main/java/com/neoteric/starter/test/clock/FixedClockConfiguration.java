package com.neoteric.starter.test.clock;

import com.neoteric.starter.StarterTestProfiles;
import com.neoteric.starter.clock.TimeZoneAutoConfiguration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import java.time.Clock;
import java.time.Instant;
import java.util.TimeZone;

@Configuration
@Profile(StarterTestProfiles.FIXED_CLOCK)
@AutoConfigureAfter(TimeZoneAutoConfiguration.class)
public class FixedClockConfiguration {

    @Value("${neostarter.time:2010-01-10T10:00:00Z}")
    private String time;

    @Bean
    @Primary
    private Clock clock() {
        return Clock.fixed(Instant.parse(time), TimeZone.getDefault().toZoneId());
    }
}