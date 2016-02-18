package com.neoteric.starter.jersey;

import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.jersey.JerseyAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;


@Configuration
@PropertySource("classpath:jersey-defaults.properties")
@AutoConfigureBefore(JerseyAutoConfiguration.class)
@EnableConfigurationProperties(NeoStarterJerseyProperties.class)
public class JerseyDefaultsAutoConfiguration {
}