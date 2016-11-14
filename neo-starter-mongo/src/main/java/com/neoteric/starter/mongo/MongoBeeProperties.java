package com.neoteric.starter.mongo;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "neostarter.mongobee")
@Getter
@Setter
public class MongoBeeProperties {
    private boolean enabled = true;
    private String uri;
    private String packageToScan;
}
