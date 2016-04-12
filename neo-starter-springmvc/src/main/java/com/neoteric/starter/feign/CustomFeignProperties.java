package com.neoteric.starter.feign;

import feign.Logger;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("neostarter.feign")
public class CustomFeignProperties {

    /**
     * Controls the level of logging. Possible values:
     *  NONE - No logging.
     *  BASIC - Log only the com.neoteric.starter.request method and URL and the response status code and execution time.
     *  HEADERS - Log the basic information along with com.neoteric.starter.request and response headers.
     *  FULL - Log the headers, body, and metadata for both requests and responses.
     */
    private Logger.Level loggerLevel = Logger.Level.BASIC;

    public Logger.Level getLoggerLevel() {
        return loggerLevel;
    }

    public void setLoggerLevel(Logger.Level loggerLevel) {
        this.loggerLevel = loggerLevel;
    }
}