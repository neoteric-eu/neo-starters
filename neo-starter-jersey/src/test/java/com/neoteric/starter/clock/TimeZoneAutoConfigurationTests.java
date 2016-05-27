package com.neoteric.starter.clock;

import com.neoteric.starter.jersey.StarterConstants;
import com.neoteric.starter.jersey.clock.TimeZoneAutoConfiguration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.time.Clock;
import java.time.ZoneId;

import static org.assertj.core.api.Assertions.*;

public class TimeZoneAutoConfigurationTests {

    private AnnotationConfigApplicationContext context;

    @Before
    public void setUp() {
        this.context = new AnnotationConfigApplicationContext();
    }

    @After
    public void tearDown() {
        if (this.context != null) {
            this.context.close();
        }
    }

    @Test
    public void clockExists() throws Exception {
        context.register(TimeZoneAutoConfiguration.class);
        context.refresh();

        assertThat(this.context.getBeanNamesForType(Clock.class).length).isEqualTo(1);
    }

    @Test
    public void isClockUTC() throws Exception {
        context.register(TimeZoneAutoConfiguration.class);
        context.refresh();

        Clock clock = context.getBean(Clock.class);
        assertThat(clock.getZone()).isEqualTo(ZoneId.of(StarterConstants.UTC));
    }
}
