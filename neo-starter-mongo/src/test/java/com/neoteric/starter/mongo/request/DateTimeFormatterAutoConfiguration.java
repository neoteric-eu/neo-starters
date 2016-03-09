package com.neoteric.starter.mongo.request;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Configuration
public class DateTimeFormatterAutoConfiguration {

    @Bean
    public DateTimeFormatter dateTimeFormatter() {
        return DateTimeFormatter.ISO_INSTANT.withZone(ZoneId.of("UTC"));
    }
}
