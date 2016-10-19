package com.neoteric.starter.mvc.logging;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.event.Level;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "neostarter.mvc.logging")
@Getter
@Setter
@SuppressWarnings("pmd.ImmutableField")
public class ApiLoggingProperties {

    private boolean enabled = true;
    private Level entryPointLevel = Level.INFO;
    private Level exitPointLevel = Level.INFO;
    private Level customParamsLevel = Level.DEBUG;
    private Level jsonApiListSizeLevel = Level.INFO;
    private Level jsonApiObjectLevel = Level.DEBUG;
}
