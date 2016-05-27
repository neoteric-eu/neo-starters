package com.neoteric.starter.test.jersey.clock;

import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;

@ToString
@EqualsAndHashCode(callSuper = true)
public class StaticClock extends Clock {

    public static Instant instant;
    public static ZoneId zone;

    StaticClock(Instant fixedInstant, ZoneId zoneId) {
        instant = fixedInstant;
        zone = zoneId;
    }
    @Override
    public ZoneId getZone() {
        return zone;
    }

    @Override
    public Clock withZone(ZoneId zoneId) {
        zone = zoneId;
        return this;
    }
    @Override
    public long millis() {
        return instant.toEpochMilli();
    }
    @Override
    public Instant instant() {
        return instant;
    }
}
