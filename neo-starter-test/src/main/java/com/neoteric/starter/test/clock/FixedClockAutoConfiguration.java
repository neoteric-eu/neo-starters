package com.neoteric.starter.test.clock;

import com.neoteric.starter.clock.TimeZoneAutoConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;

import java.time.Clock;
import java.time.Instant;
import java.util.TimeZone;

import static com.neoteric.starter.test.StarterConstants.LOG_PREFIX;
import static com.neoteric.starter.test.StarterTestProfiles.FIXED_CLOCK;

@Slf4j
@Configuration
@Profile(FIXED_CLOCK)
@AutoConfigureAfter(TimeZoneAutoConfiguration.class)
public class FixedClockAutoConfiguration {

    @Value("${neostarter.test.time:2010-01-10T10:00:00Z}")
    private String time;

    @Bean
    @Primary
    private Clock clock() {
        LOG.info("{}Setting Clock to: {}", LOG_PREFIX, time);
        return Clock.fixed(Instant.parse(time), TimeZone.getDefault().toZoneId());
    }
}