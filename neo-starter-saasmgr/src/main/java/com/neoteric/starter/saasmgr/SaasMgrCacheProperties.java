package com.neoteric.starter.saasmgr;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("neostarter.saasmgr.cache")
public class SaasMgrCacheProperties {

    /**
     * Enable SaaS Manager authentication caching
     */
    private boolean enabled = true;

    /**
     * Cache timeout in seconds
     */
    private int timeToLiveSeconds = 5;

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getTimeToLiveSeconds() {
        return timeToLiveSeconds;
    }

    public void setTimeToLiveSeconds(int timeToLiveSeconds) {
        this.timeToLiveSeconds = timeToLiveSeconds;
    }
}
