package com.neoteric.starter.jersey;

import com.neoteric.starter.StarterConstants;
import lombok.extern.slf4j.Slf4j;
import org.glassfish.jersey.server.ResourceConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.jersey.JerseyAutoConfiguration;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.Set;

import static com.neoteric.starter.StarterConstants.LOG_PREFIX;

@Configuration
@Slf4j
@AutoConfigureAfter(JerseyAutoConfiguration.class)
public class JerseyLogAutoConfiguration {

    @Autowired
    ResourceConfig resourceConfig;

    @PostConstruct
    private void postConstruct() {
        Set<Class<?>> classes = resourceConfig.getClasses();
        LOG.debug("{} Classes registered by Jersey:\n{}", LOG_PREFIX, classes.stream()
                .map(Class::getName)
                .sorted()
                .reduce("", (s, s2) -> String.join("\n", s, s2)));

    }
}
