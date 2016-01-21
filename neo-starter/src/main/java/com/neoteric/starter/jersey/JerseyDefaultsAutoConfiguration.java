package com.neoteric.starter.jersey;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.jersey.JerseyAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;


@Configuration
@PropertySource("classpath:jersey-defaults.properties")
@AutoConfigureBefore(JerseyAutoConfiguration.class)
public class JerseyDefaultsAutoConfiguration {
}
