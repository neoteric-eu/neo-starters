package com.neoteric.starter.date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

@Configuration
public class TestTimeConfiguration {

    private static final Logger LOG = LoggerFactory.getLogger(TestTimeConfiguration.class);

    @Bean
    public ZoneId zoneId() {
        LOG.error("XXXX");
        return null;
    }

    @Bean
    public Clock clock() {
        LOG.error("AAAAAAAAAA");
        return Clock.fixed(Instant.ofEpochMilli(1000), ZoneId.systemDefault());
    }
}
