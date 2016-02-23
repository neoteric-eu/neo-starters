package com.neoteric.starter.test.clock;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

@Configuration
public class TestTimeConfiguration {

    @Value("${neostarter.fixedTime:2016-01-20T10:00:00Z}")
    private String fixedTime;

    @Bean
    @Primary
    public Clock testClock() {
        return Clock.fixed(Instant.parse(fixedTime), ZoneId.systemDefault());
    }
}
